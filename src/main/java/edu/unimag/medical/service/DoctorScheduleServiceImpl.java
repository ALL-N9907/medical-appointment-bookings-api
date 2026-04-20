package edu.unimag.medical.service;


import edu.unimag.medical.api.dto.DoctorScheduleDTOs;
import edu.unimag.medical.api.dto.DoctorScheduleDTOs.*;
import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.entities.DoctorSchedule;
import edu.unimag.medical.domain.repository.DoctorRepository;
import edu.unimag.medical.domain.repository.DoctorScheduleRepository;
import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.mapper.DoctorScheduleMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleMapper doctorScheduleMapper;

    @Override
    @Transactional
    public DoctorScheduleDTOs.DoctorScheduleResponse createDoctorSchedule(UUID doctorId, DoctorScheduleDTOs.CreateDoctorScheduleRequest req) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new ResourceNotFoundException("the doctor with "+doctorId+" was not found"));
        if(doctorScheduleRepository.existsByDoctor_IdAndDayOfWeek(doctorId, req.dayOfWeek())){
            throw new ConflictException("the doctor with id "+doctorId+ " already has a schedule for "+req.dayOfWeek());
        }
        DoctorSchedule doctorSchedule = doctorScheduleMapper.toEntity(req, doctor);
        return doctorScheduleMapper.toResponse(doctorScheduleRepository.save(doctorSchedule));

    }

    @Override
    @Transactional
    public DoctorScheduleDTOs.DoctorScheduleResponse findByDoctorId_AndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek) {
        return doctorScheduleRepository.findByDoctor_IdAndDayOfWeek(doctorId,dayOfWeek).map(doctorScheduleMapper::toResponse).
                orElseThrow(() -> new ResourceNotFoundException("Doctor schedule with doctor id " +doctorId+
                                                                " and day of week "+dayOfWeek+" not found"));
    }

    @Override
    @Transactional
    public List<DoctorScheduleResponse> findAll(UUID doctorId) {
        return doctorScheduleRepository.findByDoctor_id(doctorId).stream().map(doctorScheduleMapper::toResponse).toList();
    }
}
