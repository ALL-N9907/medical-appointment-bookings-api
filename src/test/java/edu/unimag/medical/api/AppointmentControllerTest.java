package edu.unimag.medical.api;

import edu.unimag.medical.domain.enums.OfficeStatus;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import edu.unimag.medical.api.dto.AppointmentDTOs.*;
import edu.unimag.medical.api.dto.DoctorDTOs.DoctorResponse;
import edu.unimag.medical.api.dto.OfficeDTOs.OfficeResponse;
import edu.unimag.medical.api.dto.PatientDTOs.PatientResponse;
import edu.unimag.medical.api.dto.AppointmentTypeDTOs.AppointmentTypeResponse;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import edu.unimag.medical.domain.enums.PatientStatus;
import edu.unimag.medical.exception.BusinessException;
import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean AppointmentService appointmentService;

    private static final UUID ID                  = UUID.randomUUID();
    private static final UUID PATIENT_ID          = UUID.randomUUID();
    private static final UUID DOCTOR_ID           = UUID.randomUUID();
    private static final UUID OFFICE_ID           = UUID.randomUUID();
    private static final UUID APPOINTMENT_TYPE_ID = UUID.randomUUID();
    private static final LocalDateTime START_AT   = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
    private static final LocalDate DATE           = START_AT.toLocalDate();

    private static final AppointmentResponse RESPONSE = new AppointmentResponse(
            ID,
            new PatientResponse(PATIENT_ID, "John Doe", "john@email.com", "300", PatientStatus.ACTIVE),
            new DoctorResponse(DOCTOR_ID, "Dr. House", true, "Cardiology"),
            new OfficeResponse(OFFICE_ID, 101, "Floor 1", OfficeStatus.AVAILABLE),
            new AppointmentTypeResponse(APPOINTMENT_TYPE_ID, "General", 30),
            START_AT, START_AT.plusMinutes(30), DATE,
            AppointmentStatus.SCHEDULED, null, null
    );

    private CreateAppointmentRequest validRequest() {
        return new CreateAppointmentRequest(PATIENT_ID, DOCTOR_ID, OFFICE_ID, APPOINTMENT_TYPE_ID, START_AT, DATE);
    }


    @Test
    void createAppointment_shouldReturn201WithCreatedAppointment() throws Exception {
        when(appointmentService.createAppointment(any())).thenReturn(RESPONSE);

        mvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void createAppointment_shouldReturn400WhenPatientIdIsNull() throws Exception {
        var req = new CreateAppointmentRequest(null, DOCTOR_ID, OFFICE_ID, APPOINTMENT_TYPE_ID, START_AT, DATE);

        mvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAppointment_shouldReturn422WhenDoctorIsInactive() throws Exception {
        when(appointmentService.createAppointment(any()))
                .thenThrow(new BusinessException("The doctor with id " + DOCTOR_ID + " is not active"));

        mvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("The doctor with id " + DOCTOR_ID + " is not active"));
    }

    @Test
    void createAppointment_shouldReturn409WhenDoctorHasOverlappingAppointment() throws Exception {
        when(appointmentService.createAppointment(any()))
                .thenThrow(new ConflictException("The doctor already has an appointment between " + START_AT + " and " + START_AT.plusMinutes(30)));

        mvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void createAppointment_shouldReturn404WhenPatientNotFound() throws Exception {
        when(appointmentService.createAppointment(any()))
                .thenThrow(new ResourceNotFoundException("The id " + PATIENT_ID + " of the patient, was not found"));

        mvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("The id " + PATIENT_ID + " of the patient, was not found"));
    }



    @Test
    void findById_shouldReturn200WithAppointment() throws Exception {
        when(appointmentService.findByid(eq(ID))).thenReturn(RESPONSE);

        mvc.perform(get("/api/appointments/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void findById_shouldReturn404WhenAppointmentNotFound() throws Exception {
        when(appointmentService.findByid(any()))
                .thenThrow(new ResourceNotFoundException("The appointment with the id " + ID + ", was not found"));

        mvc.perform(get("/api/appointments/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("The appointment with the id " + ID + ", was not found"));
    }


    @Test
    void confirmAppointment_shouldReturn200WhenConfirmed() throws Exception {
        var confirmed = new AppointmentResponse(
                ID, RESPONSE.patient(), RESPONSE.doctor(), RESPONSE.office(),
                RESPONSE.appointmentType(), START_AT, START_AT.plusMinutes(30),
                DATE, AppointmentStatus.CONFIRMED, null, null
        );
        when(appointmentService.confirmAppointment(eq(ID))).thenReturn(confirmed);

        mvc.perform(put("/api/appointments/{id}/confirm", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirmAppointment_shouldReturn422WhenNotScheduled() throws Exception {
        when(appointmentService.confirmAppointment(any()))
                .thenThrow(new BusinessException("Only SCHEDULED appointments can be confirmed"));

        mvc.perform(put("/api/appointments/{id}/confirm", ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Only SCHEDULED appointments can be confirmed"));
    }



    @Test
    void cancelAppointment_shouldReturn200WhenCancelled() throws Exception {
        var cancelled = new AppointmentResponse(
                ID, RESPONSE.patient(), RESPONSE.doctor(), RESPONSE.office(),
                RESPONSE.appointmentType(), START_AT, START_AT.plusMinutes(30),
                DATE, AppointmentStatus.CANCELLED, "Patient request", null
        );
        when(appointmentService.cancelAppointment(eq(ID), any())).thenReturn(cancelled);

        mvc.perform(put("/api/appointments/{id}/cancel", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelAppointmentRequest("Patient request"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancellationReason").value("Patient request"));
    }

    @Test
    void cancelAppointment_shouldReturn422WhenNotScheduledOrConfirmed() throws Exception {
        when(appointmentService.cancelAppointment(any(), any()))
                .thenThrow(new BusinessException("Only SCHEDULED or CONFIRMED appointments can be cancelled"));

        mvc.perform(put("/api/appointments/{id}/cancel", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelAppointmentRequest("Patient request"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Only SCHEDULED or CONFIRMED appointments can be cancelled"));
    }

    @Test
    void setAsNoShow_shouldReturn200WhenMarkedAsNoShow() throws Exception {
        var noShow = new AppointmentResponse(
                ID, RESPONSE.patient(), RESPONSE.doctor(), RESPONSE.office(),
                RESPONSE.appointmentType(), START_AT, START_AT.plusMinutes(30),
                DATE, AppointmentStatus.NO_SHOW, null, null
        );
        when(appointmentService.setAsNoShowAppointment(eq(ID))).thenReturn(noShow);

        mvc.perform(put("/api/appointments/{id}/no-show", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_SHOW"));
    }

    @Test
    void setAsNoShow_shouldReturn422WhenBeforeStartTime() throws Exception {
        when(appointmentService.setAsNoShowAppointment(any()))
                .thenThrow(new BusinessException("Appointment cannot be mark as no-show before its start time."));

        mvc.perform(put("/api/appointments/{id}/no-show", ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Appointment cannot be mark as no-show before its start time."));
    }
}