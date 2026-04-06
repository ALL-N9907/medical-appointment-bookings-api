package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.DoctorDTOs.*;
import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.entities.Specialty;
import edu.unimag.medical.domain.repository.DoctorRepository;
import edu.unimag.medical.domain.repository.SpecialtyRepository;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.mapper.DoctorMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final SpecialtyRepository specialtyRepository;

    @Override
    @Transactional
    public DoctorResponse create(CreateDoctorRequest req) {
        Specialty specialty = specialtyRepository.findById(req.specialtyId()).
                orElseThrow(()-> new ResourceNotFoundException("the id " +req.specialtyId()+ "of the specialty was not found"));
        Doctor doctor = doctorMapper.toEntity(req, specialty);
        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public DoctorResponse update(UUID id, UpdateDoctorRequest req) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Doctor with "+id+" not found"));
        Specialty specialty = specialtyRepository.findById(req.specialtyId()).orElseThrow(
                () -> new ResourceNotFoundException("Specialty with "+id+ " not found"));

        doctor.setFullName(req.fullName());
        doctor.setSpecialty(specialty);
        doctor.setActive(req.active());
        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public DoctorResponse findById(UUID id) {
        return doctorRepository.findById(id).map(doctorMapper::toResponse).orElseThrow(
                ()-> new ResourceNotFoundException("the id "+id+ " was not found"));
    }

    @Override
    @Transactional
    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll().stream().map(doctorMapper::toResponse).toList();
    }
}
