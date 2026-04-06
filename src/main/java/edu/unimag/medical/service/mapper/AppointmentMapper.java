package edu.unimag.medical.service.mapper;

import edu.unimag.medical.api.dto.AppointmentDTOs;
import edu.unimag.medical.domain.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final OfficeMapper officeMapper;
    private final AppointmentTypeMapper appointmentTypeMapper;

    public Appointment toEntity(AppointmentDTOs.CreateAppointmentRequest req, Patient patient, Doctor doctor, Office office, AppointmentType appointmentType){
        return Appointment.builder().patient(patient).doctor(doctor).office(office).appointmentType(appointmentType).startAt(req.startAt()).date(req.date()).build();
    }

    public AppointmentDTOs.AppointmentResponse toResponse(Appointment a) {
        return new AppointmentDTOs.AppointmentResponse(
                a.getId(),
                patientMapper.toResponse(a.getPatient()),
                doctorMapper.toResponse(a.getDoctor()),
                officeMapper.toResponse(a.getOffice()),
                appointmentTypeMapper.toResponse(a.getAppointmentType()),
                a.getStartAt(), a.getEndAt(), a.getDate(),
                a.getAppointmentStatus(),
                a.getCancellationReason(), a.getObservation()
        );
    }
}