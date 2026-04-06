package edu.unimag.medical.service.mapper;

import edu.unimag.medical.api.dto.OfficeDTOs;
import edu.unimag.medical.domain.entities.Office;
import org.springframework.stereotype.Component;

@Component
public class OfficeMapper {

    public Office toEntity(OfficeDTOs.CreateOfficeRequest req) {
        return Office.builder().number(req.number()).location(req.location()).build();
    }

    public OfficeDTOs.OfficeResponse toResponse(Office o){
        return new OfficeDTOs.OfficeResponse(o.getId(), o.getNumber(), o.getLocation(), o.getOfficeStatus());
    }

    public static Office toUpdate(Office office, OfficeDTOs.UpdateOfficeRequest req) {
        if (req.number() != null) office.setNumber(req.number());
        if (req.location() != null) office.setLocation(req.location());
        if (req.status() != null) office.setOfficeStatus(req.status());
        return office;
    }


}
