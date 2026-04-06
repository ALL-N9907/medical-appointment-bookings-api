package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.PatientDTOs;
import edu.unimag.medical.api.dto.PatientDTOs.*;

import java.util.List;
import java.util.UUID;

public interface PatientService {
    PatientResponse create(PatientDTOs.CreatePatientRequest req);
    PatientResponse update(UUID id, PatientDTOs.UpdatePatientRequest req);
    PatientResponse findById(UUID id);
    List<PatientResponse> findAll();
}
