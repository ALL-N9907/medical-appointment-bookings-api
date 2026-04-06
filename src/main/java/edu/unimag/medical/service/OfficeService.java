package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.OfficeDTOs.*;
import edu.unimag.medical.api.dto.OfficeDTOs;

import java.util.List;
import java.util.UUID;


public interface OfficeService {
     OfficeResponse create(OfficeDTOs.CreateOfficeRequest req);
     OfficeResponse update(UUID id, UpdateOfficeRequest req);
     List<OfficeResponse> findAll();
}
