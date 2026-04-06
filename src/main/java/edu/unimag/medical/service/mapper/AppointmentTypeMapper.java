package edu.unimag.medical.service.mapper;

import edu.unimag.medical.api.dto.AppointmentTypeDTOs;
import edu.unimag.medical.domain.entities.AppointmentType;
import org.springframework.stereotype.Component;

@Component
public class AppointmentTypeMapper {

    public static AppointmentType toEntity(AppointmentTypeDTOs.CreateAppointmentTypeRequest req){
        return AppointmentType.builder().name(req.name()).durationMinutes(req.durationMinutes()).build();
    }

    public static AppointmentTypeDTOs.AppointmentTypeResponse toResponse(AppointmentType at){
        return new AppointmentTypeDTOs.AppointmentTypeResponse(at.getId(), at.getName(), at.getDurationMinutes());
    }

}
