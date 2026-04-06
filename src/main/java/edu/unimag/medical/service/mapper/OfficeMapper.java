package edu.unimag.medical.service.mapper;

import edu.unimag.medical.api.dto.OfficeDTOs;
import edu.unimag.medical.domain.entities.Office;
import org.springframework.stereotype.Component;

@Component
public class OfficeMapper {

    public static Office toEntity(OfficeDTOs.CreateOfficeRequest req) {
        return Office.builder().number(req.number()).location(req.location()).build();
    }

    public static OfficeDTOs.OfficeResponse toResponse(Office o){
        return new OfficeDTOs.OfficeResponse(o.getId(), o.getNumber(), o.getLocation(), o.getOfficeStatus());
    }


}
