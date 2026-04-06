package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.AppointmentTypeDTOs;
import edu.unimag.medical.domain.entities.AppointmentType;
import edu.unimag.medical.domain.repository.AppointmentTypeRepository;
import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.service.mapper.AppointmentTypeMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentTypeServiceImpl implements AppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentTypeMapper appointmentTypeMapper;

    @Override
    @Transactional
    public AppointmentTypeDTOs.AppointmentTypeResponse create(AppointmentTypeDTOs.CreateAppointmentTypeRequest req) {
        if(appointmentTypeRepository.existsByName(req.name())){
            throw new ConflictException("The name " +req.name()+ " for an Appointment Type is already exists");
        }
        AppointmentType appointmentType = appointmentTypeMapper.toEntity(req);
        return appointmentTypeMapper.toResponse(appointmentTypeRepository.save(appointmentType));
    }

    @Override
    public List<AppointmentTypeDTOs.AppointmentTypeResponse> findAll() {
        return appointmentTypeRepository.findAll().stream().map(appointmentTypeMapper::toResponse).toList();
    }
}
