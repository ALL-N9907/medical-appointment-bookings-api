package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.AvailabilityDTOs;
import edu.unimag.medical.api.error.GlobalExceptionHandler;
import edu.unimag.medical.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTest {

    private MockMvc mvc;

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private AvailabilityController availabilityController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(availabilityController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAvailabilitySlot() throws Exception {
        var doctorId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var date = LocalDate.now().plusDays(1);

        var slots = List.of(
                new AvailabilityDTOs.AvailabilitySlotResponse(LocalDateTime.of(date, LocalTime.of(9, 0)),
                        LocalDateTime.of(date, LocalTime.of(9, 30))),
                new AvailabilityDTOs.AvailabilitySlotResponse(LocalDateTime.of(date, LocalTime.of(10, 0)),
                        LocalDateTime.of(date, LocalTime.of(10, 30)))
        );

        when(availabilityService.getAvailabilitySlot(eq(doctorId), eq(date), eq(appointmentTypeId))).thenReturn(slots);

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", date.toString())
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    void getAvailabilitySlots_NoSlotsAvailable() throws Exception {
        var doctorId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var date = LocalDate.now().plusDays(1);

        when(availabilityService.getAvailabilitySlot(eq(doctorId), eq(date), eq(appointmentTypeId)))
                .thenReturn(List.of());

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", date.toString())
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAvailabilitySlots_whenDoctorNotFound() throws Exception {
        var doctorId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var date = LocalDate.now().plusDays(1);

        when(availabilityService.getAvailabilitySlot(eq(doctorId), eq(date), eq(appointmentTypeId)))
                .thenThrow(new RuntimeException("Doctor not found with id: " + doctorId));

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", date.toString())
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test void getAvailabilitySlots_whenScheduleConflict() throws Exception {
        var doctorId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var date = LocalDate.now().plusDays(1);

        when(availabilityService.getAvailabilitySlot(eq(doctorId), eq(date), eq(appointmentTypeId)))
                .thenThrow(new RuntimeException("Schedule conflict: Doctor already has appointments"));

        mvc.perform(get("/api/availability/doctors/{doctorId}", doctorId)
                        .param("date", date.toString())
                        .param("appointmentTypeId", appointmentTypeId.toString()))
                .andExpect(status().isConflict());
    }
}