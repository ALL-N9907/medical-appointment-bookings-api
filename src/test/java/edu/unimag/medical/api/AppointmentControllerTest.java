package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.*;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import edu.unimag.medical.domain.enums.OfficeStatus;
import edu.unimag.medical.domain.enums.PatientStatus;
import edu.unimag.medical.service.AppointmentService;
import edu.unimag.medical.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private MockMvc mvc;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
    }

    @Test
    void createAppointment() throws Exception {
        var appointmentId = UUID.randomUUID();
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = LocalDateTime.now().plusDays(1);
        var date = LocalDate.now().plusDays(1);

        var patientResponse =
                new PatientDTOs.PatientResponse(
                patientId, "Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE);
        var doctorResponse =
                new DoctorDTOs.DoctorResponse(
                doctorId, "John Doe", true, "Cardiology");
        var officeResponse =
                new OfficeDTOs.OfficeResponse(officeId, 101, "Building 1", OfficeStatus.AVAILABLE);
        var appointmentTypeResponse =
                new AppointmentTypeDTOs.AppointmentTypeResponse(
                        appointmentTypeId, "General Checkup", 30);

        var response = new AppointmentDTOs.AppointmentResponse(
                appointmentId, patientResponse, doctorResponse, officeResponse,
                appointmentTypeResponse, startAt, startAt.plusMinutes(30), date,
                AppointmentStatus.SCHEDULED, null, null
        );

        when(appointmentService.createAppointment(any(AppointmentDTOs.CreateAppointmentRequest.class))).thenReturn(response);

        String jsonRequest = "{\"patientId\":\"" + patientId +
                "\",\"doctorId\":\"" + doctorId +
                "\",\"officeId\":\"" + officeId +
                "\",\"appointmentTypeId\":\"" + appointmentTypeId +
                "\",\"startAt\":\"" + startAt.toString() +
                "\",\"date\":\"" + date.toString() + "\"}";

        mvc.perform(post("/api/appointments")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/appointments/" + appointmentId)))
                .andExpect(jsonPath("$.id").value(appointmentId.toString()))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void createAppointment_validationError() throws Exception {
        String invalidJson = "{\"patientId\":null,\"doctorId\":null,\"officeId\":null,\"appointmentTypeId\":null,\"startAt\":null,\"date\":null}";

        mvc.perform(post("/api/appointments")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAppointment_scheduleConflict() throws Exception {
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = LocalDateTime.now().plusDays(1);
        var date = LocalDate.now().plusDays(1);

        when(appointmentService.createAppointment(any(AppointmentDTOs.CreateAppointmentRequest.class)))
                .thenThrow(new RuntimeException("Schedule conflict: Doctor already has an appointment at this time"));

        String jsonRequest = "{\"patientId\":\"" + patientId +
                "\",\"doctorId\":\"" + doctorId +
                "\",\"officeId\":\"" + officeId +
                "\",\"appointmentTypeId\":\"" + appointmentTypeId +
                "\",\"startAt\":\"" + startAt.toString() +
                "\",\"date\":\"" + date.toString() + "\"}";

        mvc.perform(post("/api/appointments")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void findById() throws Exception {
        var appointmentId = UUID.randomUUID();
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = LocalDateTime.now().plusDays(1);
        var date = LocalDate.now().plusDays(1);

        var patientResponse =
                new PatientDTOs.PatientResponse(
                        patientId, "Maria Gonzalez",
                            "maria@mail.com", "555123456", PatientStatus.ACTIVE);
        var doctorResponse =
                new DoctorDTOs.DoctorResponse(
                        doctorId, "John Doe", true, "Cardiology");
        var officeResponse =
                new OfficeDTOs.OfficeResponse(
                        officeId, 101, "Building 1", OfficeStatus.AVAILABLE);
        var appointmentTypeResponse =
                new AppointmentTypeDTOs.AppointmentTypeResponse(
                        appointmentTypeId, "General Checkup", 30);

        var response = new AppointmentDTOs.AppointmentResponse(
                appointmentId, patientResponse, doctorResponse, officeResponse,
                appointmentTypeResponse, startAt, startAt.plusMinutes(30), date,
                AppointmentStatus.SCHEDULED, null, null
        );

        when(appointmentService.findByid(appointmentId)).thenReturn(response);

        mvc.perform(get("/api/appointments/{id}", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentId.toString()))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void findByIdNotFound() throws Exception {
        var appointmentId = UUID.randomUUID();

        when(appointmentService.findByid(appointmentId))
                .thenThrow(new RuntimeException("Appointment not found with id: " + appointmentId));

        mvc.perform(get("/api/appointments/{id}", appointmentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll() throws Exception{
        var appointmentId1 = UUID.randomUUID();
        var appointmentId2 = UUID.randomUUID();
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = LocalDateTime.now().plusDays(1);
        var date = LocalDate.now().plusDays(1);

        var patientResponse =
                new PatientDTOs.PatientResponse(
                        patientId, "Maria Gonzalez",
                        "maria@mail.com", "555123456", PatientStatus.ACTIVE);
        var doctorResponse =
                new DoctorDTOs.DoctorResponse(
                        doctorId, "John Doe", true, "Cardiology");
        var officeResponse =
                new OfficeDTOs.OfficeResponse(
                        officeId, 101, "Building 1", OfficeStatus.AVAILABLE);
        var appointmentTypeResponse =
                new AppointmentTypeDTOs.AppointmentTypeResponse(
                        appointmentTypeId, "General Checkup", 30);

        var appointments = List.of(
                new AppointmentDTOs.AppointmentResponse(
                        appointmentId1, patientResponse, doctorResponse, officeResponse,
                        appointmentTypeResponse, startAt, startAt.plusMinutes(30), date,
                        AppointmentStatus.SCHEDULED, null, null),
                new AppointmentDTOs.AppointmentResponse(
                        appointmentId2, patientResponse, doctorResponse, officeResponse,
                        appointmentTypeResponse, startAt.plusDays(1),
                        startAt.plusDays(1).plusMinutes(30), date.plusDays(1),
                        AppointmentStatus.CONFIRMED, null, null)
        );

        when(appointmentService.findAll()).thenReturn(appointments);

        mvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));
    }

    @Test
    void confirmAppointment() throws Exception {
        var appointmentId = UUID.randomUUID();
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = LocalDateTime.now().plusDays(1);
        var date = LocalDate.now().plusDays(1);

        var patientResponse =
                new PatientDTOs.PatientResponse(
                        patientId, "Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE);
        var doctorResponse =
                new DoctorDTOs.DoctorResponse(
                        doctorId, "John Doe", true, "Cardiology");
        var officeResponse =
                new OfficeDTOs.OfficeResponse(
                        officeId, 101, "Building 1", OfficeStatus.AVAILABLE);
        var appointmentTypeResponse =
                new AppointmentTypeDTOs.AppointmentTypeResponse(
                        appointmentTypeId, "General Checkup", 30);

        var response = new AppointmentDTOs.AppointmentResponse(
                appointmentId, patientResponse, doctorResponse, officeResponse,
                appointmentTypeResponse, startAt, startAt.plusMinutes(30), date,
                AppointmentStatus.CONFIRMED, null, null
        );

        when(appointmentService.confirmAppointment(appointmentId)).thenReturn(response);

        mvc.perform(put("/api/appointments/{id}/confirm", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void confirmAppointmentNotFound() throws Exception {
        var appointmentId = UUID.randomUUID();

        when(appointmentService.confirmAppointment(appointmentId))
                .thenThrow(new RuntimeException("Appointment not found with id: " + appointmentId));

        mvc.perform(put("/api/appointments/{id}/confirm", appointmentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelAppointment() throws Exception {
        var appointmentId = UUID.randomUUID();
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = LocalDateTime.now().plusDays(1);
        var date = LocalDate.now().plusDays(1);

        var patientResponse =
                new PatientDTOs.PatientResponse(
                        patientId, "Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE);
        var doctorResponse =
                new DoctorDTOs.DoctorResponse(
                        doctorId, "John Doe", true, "Cardiology");
        var officeResponse =
                new OfficeDTOs.OfficeResponse(
                        officeId, 101, "Building 1", OfficeStatus.AVAILABLE);
        var appointmentTypeResponse =
                new AppointmentTypeDTOs.AppointmentTypeResponse(
                        appointmentTypeId, "General Checkup", 30);

        var response = new AppointmentDTOs.AppointmentResponse(
                appointmentId, patientResponse, doctorResponse, officeResponse,
                appointmentTypeResponse, startAt, startAt.plusMinutes(30), date,
                AppointmentStatus.CANCELLED, "Patient requested cancellation", null
        );

        when(appointmentService
                .cancelAppointment(eq(appointmentId),
                        any(AppointmentDTOs.CancelAppointmentRequest.class))).thenReturn(response);

        String jsonRequest = "{\"cancellationReason\":\"Patient requested cancellation\"}";

        mvc.perform(put("/api/appointments/{id}/cancel", appointmentId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath(
                        "$.cancellationReason").value("Patient requested cancellation"));
    }

    @Test
    void cancelAppointmentNotFound() throws Exception {
        var appointmentId = UUID.randomUUID();

        when(appointmentService.cancelAppointment(eq(appointmentId), any(AppointmentDTOs.CancelAppointmentRequest.class)))
                .thenThrow(new RuntimeException("Appointment not found with id: " + appointmentId));

        String jsonRequest = "{\"cancellationReason\":\"Patient requested cancellation\"}";

        mvc.perform(put("/api/appointments/{id}/cancel", appointmentId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }


    @Test
    void completeAppointment() throws Exception {
        var appointmentId = UUID.randomUUID();
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = LocalDateTime.now().plusDays(1);
        var date = LocalDate.now().plusDays(1);

        var patientResponse =
                new PatientDTOs.PatientResponse(
                        patientId, "Maria Gonzalez",
                        "maria@mail.com", "555123456", PatientStatus.ACTIVE);
        var doctorResponse =
                new DoctorDTOs.DoctorResponse(
                        doctorId, "John Doe", true, "Cardiology");
        var officeResponse =
                new OfficeDTOs.OfficeResponse(
                        officeId, 101, "Building 1", OfficeStatus.AVAILABLE);
        var appointmentTypeResponse =
                new AppointmentTypeDTOs.AppointmentTypeResponse(
                        appointmentTypeId, "General Checkup", 30);

        var response = new AppointmentDTOs.AppointmentResponse(
                appointmentId, patientResponse, doctorResponse, officeResponse,
                appointmentTypeResponse, startAt, startAt.plusMinutes(30), date,
                AppointmentStatus.COMPLETED, null, "Treatment completed"
        );

        when(appointmentService.completeAppointment(eq(appointmentId), any(AppointmentDTOs.CompleteAppointmentRequest.class))).thenReturn(response);

        String jsonRequest = "{\"observations\":\"Treatment completed\"}";

        mvc.perform(put("/api/appointments/{id}/complete", appointmentId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.observations").value("Treatment completed"));
    }

    @Test
    void completeAppointment_appointmentNotFound() throws Exception {
        var appointmentId = UUID.randomUUID();

        when(appointmentService.completeAppointment(eq(appointmentId), any(AppointmentDTOs.CompleteAppointmentRequest.class)))
                .thenThrow(new RuntimeException("Appointment not found with id: " + appointmentId));

        String jsonRequest = "{\"observations\":\"Treatment completed\"}";

        mvc.perform(put("/api/appointments/{id}/complete", appointmentId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void setAsNoShowAppointment() throws Exception {
        var appointmentId = UUID.randomUUID();
        var patientId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var officeId = UUID.randomUUID();
        var appointmentTypeId = UUID.randomUUID();
        var startAt = LocalDateTime.now().plusDays(1);
        var date = LocalDate.now().plusDays(1);

        var patientResponse =
                new PatientDTOs.PatientResponse(
                        patientId, "Maria Gonzalez",
                        "maria@mail.com", "555123456", PatientStatus.ACTIVE);
        var doctorResponse =
                new DoctorDTOs.DoctorResponse(
                        doctorId, "John Doe", true, "Cardiology");
        var officeResponse =
                new OfficeDTOs.OfficeResponse(
                        officeId, 101, "Building 1", OfficeStatus.AVAILABLE);
        var appointmentTypeResponse =
                new AppointmentTypeDTOs.AppointmentTypeResponse(
                        appointmentTypeId, "General Checkup", 30);

        var response = new AppointmentDTOs.AppointmentResponse(
                appointmentId, patientResponse, doctorResponse, officeResponse,
                appointmentTypeResponse, startAt, startAt.plusMinutes(30), date,
                AppointmentStatus.NO_SHOW, null, null
        );

        when(appointmentService.setAsNoShowAppointment(appointmentId)).thenReturn(response);

        mvc.perform(put("/api/appointments/{id}/no-show", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_SHOW"));
    }

    @Test
    void setAsNoShowAppointment_appointmentNotFound() throws Exception {
        var appointmentId = UUID.randomUUID();

        when(appointmentService.setAsNoShowAppointment(appointmentId))
                .thenThrow(new RuntimeException("Appointment not found with id: " + appointmentId));

        mvc.perform(put("/api/appointments/{id}/no-show", appointmentId))
                .andExpect(status().isNotFound());
    }
}