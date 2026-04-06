package edu.unimag.medical.service.mapper;

import edu.unimag.medical.api.dto.DoctorDTOs;
import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.entities.Specialty;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public Doctor toEntity(DoctorDTOs.CreateDoctorRequest req, Specialty specialty) {
        return Doctor.builder().fullName(req.fullName()).active(true).specialty(specialty).build();
    }

    public DoctorDTOs.DoctorResponse toResponse(Doctor d){
        return new DoctorDTOs.DoctorResponse(d.getId(), d.getFullName(), d.isActive(), d.getSpecialty().getName());
    }
}
