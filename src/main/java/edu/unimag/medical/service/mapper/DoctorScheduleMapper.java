package edu.unimag.medical.service.mapper;

import edu.unimag.medical.api.dto.DoctorScheduleDTOs;
import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.entities.DoctorSchedule;
import org.springframework.stereotype.Component;

@Component
public class DoctorScheduleMapper {

    public static DoctorSchedule toEntity(DoctorScheduleDTOs.CreateDoctorScheduleRequest req, Doctor doctor){
        return DoctorSchedule.builder().startAt(req.startAt()).endAt(req.endAt()).dayOfWeek(req.dayOfWeek()).doctor(doctor).build();
    }
    public static DoctorScheduleDTOs.DoctorScheduleResponse toResponse(DoctorSchedule ds){
        return new DoctorScheduleDTOs.DoctorScheduleResponse(ds.getId(), ds.getStartAt(), ds.getEndAt(), ds.getDayOfWeek());
    }

}
