package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.AvailabilityDTOs.AvailabilitySlotResponse;
import edu.unimag.medical.exception.BusinessException;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)

class AvailabilityControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AvailabilityService availabilityService;

    @Test
    void getAvailabilitySlot_shouldReturn200WithSlots() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 4, 27);

        var slots = List.of(
                new AvailabilitySlotResponse(
                        LocalDateTime.of(2026, 4, 27, 8, 0),
                        LocalDateTime.of(2026, 4, 27, 8, 30)
                ),
                new AvailabilitySlotResponse(
                        LocalDateTime.of(2026, 4, 27, 8, 30),
                        LocalDateTime.of(2026, 4, 27, 9, 0)
                )
        );

        when(availabilityService.getAvailabilitySlot(eq(doctorId), eq(date), eq(appointmentTypeId)))
                .thenReturn(slots);

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "2026-04-27")
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].startAt").value("2026-04-27T08:00:00"))
                .andExpect(jsonPath("$[0].endAt").value("2026-04-27T08:30:00"))
                .andExpect(jsonPath("$[1].startAt").value("2026-04-27T08:30:00"))
                .andExpect(jsonPath("$[1].endAt").value("2026-04-27T09:00:00"));
    }

    @Test
    void getAvailabilitySlot_shouldReturn200WithEmptyListWhenNoSlots() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        when(availabilityService.getAvailabilitySlot(any(), any(), any()))
                .thenReturn(List.of());

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "2026-04-27")
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAvailabilitySlot_shouldReturn404WhenDoctorNotFound() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        when(availabilityService.getAvailabilitySlot(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Doctor with ID " + doctorId + " was not found"));

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "2026-04-27")
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Doctor with ID " + doctorId + " was not found"));
    }

    @Test
    void getAvailabilitySlot_shouldReturn404WhenAppointmentTypeNotFound() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        when(availabilityService.getAvailabilitySlot(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("AppointmentType with ID " + appointmentTypeId + " was not found"));

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "2026-04-27")
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("AppointmentType with ID " + appointmentTypeId + " was not found"));
    }

    @Test
    void getAvailabilitySlot_shouldReturn422WhenDoctorIsInactive() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        when(availabilityService.getAvailabilitySlot(any(), any(), any()))
                .thenThrow(new BusinessException("Doctor with ID " + doctorId + " is not active and cannot receive appointments"));

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "2026-04-27")
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Doctor with ID " + doctorId + " is not active and cannot receive appointments"));
    }

    @Test
    void getAvailabilitySlot_shouldReturn422WhenDateIsInThePast() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        when(availabilityService.getAvailabilitySlot(any(), any(), any()))
                .thenThrow(new BusinessException("Cannot check availability for past dates"));

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "2024-01-01")
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Cannot check availability for past dates"));
    }

    @Test
    void getAvailabilitySlot_shouldReturn400WhenDateParamMissing() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAvailabilitySlot_shouldReturn400WhenAppointmentTypeIdParamMissing() throws Exception {
        UUID doctorId = UUID.randomUUID();

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "2026-04-27"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAvailabilitySlot_shouldReturn400WhenDateFormatIsInvalid() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "27-04-2026")
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAvailabilitySlot_shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        when(availabilityService.getAvailabilitySlot(any(), any(), any()))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", "2026-04-27")
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
}