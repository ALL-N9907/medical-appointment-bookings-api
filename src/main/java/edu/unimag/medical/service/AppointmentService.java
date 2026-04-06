package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.AppointmentDTOs.*;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    AppointmentResponse createAppointment(CreateAppointmentRequest req);
    AppointmentResponse findByid(UUID id);
    List<AppointmentResponse> findAll();
    AppointmentResponse confirmAppointment(UUID id);
    AppointmentResponse cancelAppointment(UUID id, CancelAppointmentRequest req);
    AppointmentResponse completeAppointment(UUID id, CompleteAppointmentRequest req);
    AppointmentResponse setAsNoShowAppointment(UUID id);

}
