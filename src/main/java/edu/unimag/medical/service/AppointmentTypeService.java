package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.AppointmentTypeDTOs;
import edu.unimag.medical.api.dto.AppointmentTypeDTOs.*;

import java.util.List;

public interface AppointmentTypeService {
    AppointmentTypeResponse create(AppointmentTypeDTOs.CreateAppointmentTypeRequest req);
    List<AppointmentTypeResponse> findAll();
}
