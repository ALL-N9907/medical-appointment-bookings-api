package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.AvailabilityDTOs.*;
import edu.unimag.medical.api.dto.DoctorScheduleDTOs.*;
import edu.unimag.medical.domain.entities.AppointmentType;
import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import edu.unimag.medical.domain.repository.AppointmentRepository;
import edu.unimag.medical.domain.repository.AppointmentTypeRepository;
import edu.unimag.medical.domain.repository.DoctorRepository;
import edu.unimag.medical.exception.BusinessException;
import edu.unimag.medical.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService{

    private final DoctorScheduleService doctorScheduleService;
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    private static final List<AppointmentStatus> ACTIVE_STATUSES=
            List.of(AppointmentStatus.SCHEDULED,AppointmentStatus.CONFIRMED);

    @Override
    @Transactional
    public List<AvailabilitySlotResponse> getAvailabilitySlot(UUID doctorId, LocalDate date, UUID appointmentTypeId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with ID " + doctorId + " was not found"));

        if (!doctor.isActive()) {
            throw new BusinessException("Doctor with ID " + doctorId + " is not active and cannot receive appointments");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new BusinessException("Cannot check availability for past dates");
        }


        DoctorScheduleResponse schedule = doctorScheduleService.findByDoctorId_AndDayOfWeek(doctorId, date.getDayOfWeek());

        var bookedAppointments = appointmentRepository.findByDoctor_IdAndDate(doctorId, date, ACTIVE_STATUSES);
        AppointmentType appointmentType = appointmentTypeRepository.findById(appointmentTypeId).
                orElseThrow(() -> new ResourceNotFoundException("the id " + appointmentTypeId + " of the appointment type, was not found"));

        int slotDurationMinutes = appointmentType.getDurationMinutes();

        if (slotDurationMinutes <= 0) {
            throw new BusinessException("Appointment type duration must be greater than zero");
        }

        List<AvailabilitySlotResponse> availabilitySlots = new ArrayList<>();
        LocalTime current = schedule.startAt();
        LocalTime workEndTime = schedule.endAt();

        while(!current.plusMinutes(slotDurationMinutes).isAfter(workEndTime)) {
            LocalDateTime slotStart = LocalDateTime.of(date,current);
            LocalDateTime slotEnd = slotStart.plusMinutes(slotDurationMinutes);

            boolean isBooked = bookedAppointments.stream().anyMatch(
                    appointment -> appointment.getStartAt().isBefore(slotEnd) && appointment.getEndAt().isAfter(slotStart)
            );

            if (!isBooked) {
                availabilitySlots.add(new AvailabilitySlotResponse(slotStart, slotEnd));
            }
            current = current.plusMinutes(slotDurationMinutes);


        }

        return availabilitySlots;
    }

}
