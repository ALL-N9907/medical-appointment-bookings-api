package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.SpecialtyDTOs.CreateSpecialtyRequest;
import edu.unimag.medical.api.dto.SpecialtyDTOs.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {
    SpecialtyResponse create(CreateSpecialtyRequest req);
    List<SpecialtyResponse> findAll();
}
