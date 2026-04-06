package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.AvailabilityDTOs.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {

    List<AvailabilitySlotResponse> getAvailabilitySlot(UUID doctorId, LocalDate date, UUID appointmentTypeId);

}
