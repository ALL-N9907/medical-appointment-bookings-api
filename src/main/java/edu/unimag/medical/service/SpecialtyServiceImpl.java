package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.SpecialtyDTOs;
import edu.unimag.medical.domain.entities.Specialty;
import edu.unimag.medical.domain.repository.SpecialtyRepository;
import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.service.mapper.SpecialtyMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    @Transactional
    public SpecialtyDTOs.SpecialtyResponse create(SpecialtyDTOs.CreateSpecialtyRequest req) {
        if(specialtyRepository.existsByName(req.name())){
            throw new ConflictException(req.name() + "already exists");
        }
        Specialty specialty = specialtyMapper.toEntity(req);
        return specialtyMapper.toResponse(specialtyRepository.save(specialty));
    }

    @Override
    @Transactional
    public List<SpecialtyDTOs.SpecialtyResponse> findAll() {
        return specialtyRepository.findAll().stream().map(specialtyMapper::toResponse).toList();
    }
}
