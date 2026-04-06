package edu.unimag.medical.service.mapper;

import edu.unimag.medical.api.dto.PatientDTOs;
import edu.unimag.medical.domain.entities.Patient;
import edu.unimag.medical.domain.enums.PatientStatus;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public static Patient toEntity(PatientDTOs.CreatePatientRequest req) {
        return Patient.builder().fullName(req.fullName()).email(req.email()).phone(req.phone()).patientStatus(PatientStatus.ACTIVE).build();
    }

    public static PatientDTOs.PatientResponse toResponse(Patient p){
        return new PatientDTOs.PatientResponse(p.getId(), p.getFullName(), p.getEmail(), p.getPhone(), p.getPatientStatus());
    }
}
