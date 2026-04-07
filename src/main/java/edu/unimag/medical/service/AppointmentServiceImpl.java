package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.AppointmentDTOs;
import edu.unimag.medical.api.dto.AppointmentDTOs.*;
import edu.unimag.medical.domain.entities.*;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import edu.unimag.medical.domain.enums.OfficeStatus;
import edu.unimag.medical.domain.enums.PatientStatus;
import edu.unimag.medical.domain.repository.*;
import edu.unimag.medical.exception.BusinessException;
import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.mapper.AppointmentMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final OfficeRepository officeRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorScheduleService doctorScheduleService;

    private static final List<AppointmentStatus> ACTIVE_STATUSES =
            List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

    @Override
    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest req) {
        Patient patient = patientRepository.findById(req.patientId()).
                orElseThrow(()-> new ResourceNotFoundException("The id "+req.patientId()+ " of the patient, was not found"));
        if (patient.getPatientStatus() != PatientStatus.ACTIVE){
            throw new BusinessException ("The patient with the "+req.patientId()+" id, is not active");
        }

        Doctor doctor = doctorRepository.findById(req.doctorId()).
                orElseThrow(()-> new ResourceNotFoundException("The id "+req.doctorId()+" of the doctor, was not found"));
        if (!doctor.isActive()){
            throw new BusinessException ("The doctor with id "+req.doctorId()+ " is not active");
        }

        Office office = officeRepository.findById(req.officeId()).
                orElseThrow(()->new ResourceNotFoundException("The id "+req.officeId()+" of the office, was no found"));
        if (office.getOfficeStatus() != OfficeStatus.AVAILABLE){
            throw new BusinessException("The office with id "+req.officeId()+" is not available");
        }

        AppointmentType appointmentType = appointmentTypeRepository.findById(req.appointmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("The id "+req.appointmentTypeId()+" of the appointment type, was not found"));

        LocalDateTime endAt = req.startAt().plusMinutes(appointmentType.getDurationMinutes());
        if (!req.startAt().isAfter(LocalDateTime.now())){
            throw new BusinessException("The appointment date and time must be in the future");
        }

        var schedule = doctorScheduleRepository.findByDoctor_IdAndDayOfWeek(
                        doctor.getId(), req.date().getDayOfWeek())
                .orElseThrow(() -> new BusinessException(
                        "The doctor has no schedule for " + req.date().getDayOfWeek()));

        if(req.startAt().toLocalTime().isBefore(schedule.getStartAt()) || endAt.toLocalTime().isAfter(schedule.getEndAt())) {
            throw new BusinessException("The appointment is outside the doctor's working hours");
        }

        if (appointmentRepository.existsOverlappingForDoctor(req.doctorId(), req.date() ,req.startAt(), endAt, ACTIVE_STATUSES)){
            throw new ConflictException("The doctor already has an appointment between " + req.startAt() + " and " + endAt + " on " + req.date());
        }

        if(appointmentRepository.existsOverlappingForOffice(req.officeId(),req.date(), req.startAt(),endAt, ACTIVE_STATUSES)){
            throw new ConflictException("The office already has an appointment between " + req.startAt() + " and " + endAt + " on " + req.date());
        }

        if(appointmentRepository.existsOverlappingForPatient(req.patientId(),req.date(),req.startAt(), endAt, ACTIVE_STATUSES)){
            throw new ConflictException("The patient already has an appointment between " + req.startAt() + " and " + endAt + " on " + req.date());
        }

        Appointment appointment = appointmentMapper.toEntity(req, patient, doctor, office, appointmentType);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));

    }



    @Override
    @Transactional
    public AppointmentResponse findByid(UUID id) {
        return appointmentRepository.findById(id).map(appointmentMapper::toResponse).
                orElseThrow(()-> new ResourceNotFoundException("The appointment with the id " +id+ ", was not foud"));
    }

    @Override
    @Transactional
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream().map(appointmentMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public AppointmentResponse confirmAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("The appointment with the id " +id+ ", was not found"));

        if (appointment.getAppointmentStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Only SCHEDULED appointments can be confirmed");
        }

        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(UUID id, AppointmentDTOs.CancelAppointmentRequest req) {
        Appointment appointment = appointmentRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("the appointment with the id " +id+ ", was not found"));

        if (appointment.getAppointmentStatus() != AppointmentStatus.SCHEDULED &&
                appointment.getAppointmentStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessException("Only SCHEDULED or CONFIRMED appointments can be cancelled");
        }

        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(req.cancellationReason());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse completeAppointment(UUID id, AppointmentDTOs.CompleteAppointmentRequest req) {
        Appointment appointment = appointmentRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("the appointment with the id " +id+ ",was not found"));

        if (appointment.getAppointmentStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED appointments can be completed");
        }

        if (LocalDateTime.now().isBefore(appointment.getStartAt())){
            throw new BusinessException("Appointment can´t be completed before its start time.");
        }

        appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
        appointment.setObservation(req.observations());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse setAsNoShowAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("the appointment with the id " +id+ ",was not found"));

        if (LocalDateTime.now().isBefore(appointment.getStartAt())){
            throw new BusinessException("Appointment cannot be mark as no-show before its start time.");
        }
        appointment.setAppointmentStatus(AppointmentStatus.NO_SHOW);
        return  appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }
}