package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.PatientDTOs.*;
import edu.unimag.medical.api.dto.PatientDTOs;
import edu.unimag.medical.domain.entities.Patient;
import edu.unimag.medical.domain.repository.PatientRepository;
import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.mapper.PatientMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientResponse create(CreatePatientRequest req) {
        if(patientRepository.existsByEmail(req.email())) {
            throw new ConflictException("The email "+req.email()+" of the patient, already exits");
        }
        Patient patient = patientMapper.toEntity(req);
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public PatientResponse update(UUID id, PatientDTOs.UpdatePatientRequest req) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Patient with "+id+" not found"));
        if (!patient.getEmail().equals(req.email()) && patientRepository.existsByEmail(req.email())) {
            throw new ConflictException("A patient with "+req.email()+ " already exists");
        }
        if(!patient.getPhone().equals(req.phone()) && patientRepository.existsByPhone(req.phone())){
            throw new ConflictException("A patient with "+req.phone()+" already exists");
        }

        patient.setFullName(req.fullName());
        patient.setEmail(req.email());
        patient.setPhone(req.phone());
        patient.setPatientStatus(req.status());

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public PatientResponse findById(UUID id) {
        return patientRepository.findById(id).map(patientMapper::toResponse).
               orElseThrow(()-> new ResourceNotFoundException("the id "+id+" was not found"));
    }

    @Override
    @Transactional
    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream().map(patientMapper::toResponse).toList();
    }
}
