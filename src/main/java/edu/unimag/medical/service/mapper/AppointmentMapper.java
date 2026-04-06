package edu.unimag.medical.service.mapper;

import edu.unimag.medical.api.dto.AppointmentDTOs;
import edu.unimag.medical.domain.entities.*;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public static Appointment toEntity(AppointmentDTOs.CreateAppointmentRequest req, Patient patient, Doctor doctor, Office office, AppointmentType appointmentType){
        return Appointment.builder().patient(patient).doctor(doctor).office(office).appointmentType(appointmentType).startAt(req.startAt()).date(req.date()).build();
    }

    public static AppointmentDTOs.AppointmentResponse toResponse(Appointment a){
        return new AppointmentDTOs.AppointmentResponse(a.getId(),PatientMapper.toResponse(a.getPatient()),DoctorMapper.toResponse(a.getDoctor())
        ,OfficeMapper.toResponse(a.getOffice()),AppointmentTypeMapper.toResponse(a.getAppointmentType()),a.getStartAt(),a.getEndAt(),a.getDate(),a.getAppointmentStatus()
                ,a.getCancellationReason(),a.getObservation()
        );
    }

}
