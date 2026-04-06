package edu.unimag.medical.service.mapper;


import edu.unimag.medical.api.dto.SpecialtyDTOs;
import edu.unimag.medical.domain.entities.Specialty;
import org.springframework.stereotype.Component;

@Component
public class SpecialtyMapper {
    public static Specialty toEntity(SpecialtyDTOs.CreateSpecialtyRequest req){
        return Specialty.builder().name(req.name()).build();
    }

    public static SpecialtyDTOs.SpecialtyResponse toResponse(Specialty s){
        return new SpecialtyDTOs.SpecialtyResponse(s.getId(), s.getName());
    }

}
