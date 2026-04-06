package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.DoctorDTOs;
import edu.unimag.medical.api.dto.DoctorDTOs.*;

import java.util.List;
import java.util.UUID;


public interface DoctorService {
   DoctorResponse create(DoctorDTOs.CreateDoctorRequest req);
   DoctorResponse update(UUID id, UpdateDoctorRequest req);
   DoctorResponse findById(UUID id);
   List<DoctorResponse> findAll();

}
