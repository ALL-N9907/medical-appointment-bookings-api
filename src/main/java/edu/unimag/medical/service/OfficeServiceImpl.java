package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.OfficeDTOs.*;
import edu.unimag.medical.api.dto.OfficeDTOs;
import edu.unimag.medical.domain.entities.Office;
import edu.unimag.medical.domain.repository.OfficeRepository;
import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.mapper.OfficeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    @Override
    @Transactional
    public OfficeDTOs.OfficeResponse create(OfficeDTOs.CreateOfficeRequest req) {
        if(officeRepository.existsByNumber(req.number())){
            throw new ConflictException("Office number "+req.number()+ " already exists");
        }
        Office office = officeMapper.toEntity(req);
        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Override
    @Transactional
    public OfficeDTOs.OfficeResponse update(UUID id, OfficeDTOs.UpdateOfficeRequest req) {
        Office office = officeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("the id " + id + " of the office was not found"));
        if (!office.getNumber().equals(req.number()) && officeRepository.existsByNumber(req.number())){
            throw new ConflictException("Office number " +req.number()+ " already exists");
        }
        office.setNumber(req.number());
        office.setLocation(req.location());
        office.setOfficeStatus(req.status());

        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Override
    @Transactional
    public List<OfficeResponse> findAll() {
        return officeRepository.findAll().stream().map(officeMapper::toResponse).toList();
    }
}
