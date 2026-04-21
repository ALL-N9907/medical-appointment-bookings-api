package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.PatientDTOs;
import edu.unimag.medical.domain.enums.PatientStatus;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    private MockMvc mvc;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(patientController).build();
    }

    @Test
    void create() throws Exception {
        var patientId = UUID.randomUUID();
        var request = new PatientDTOs.CreatePatientRequest("Maria Gonzalez", "maria@mail.com", "555123456");
        var response = new PatientDTOs.PatientResponse(
                patientId, "Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE);

        when(patientService.create(any(PatientDTOs.CreatePatientRequest.class))).thenReturn(response);

        mvc.perform(post("/api/patients")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/patients/" + patientId)))
                .andExpect(jsonPath("$.id").value(patientId.toString()))
                .andExpect(jsonPath("$.fullName").value("Maria Gonzalez"));

    }


    @Test
    void create_validationError() throws Exception {
        var invalidRequest = new PatientDTOs.CreatePatientRequest("","invalid email","111");

        mvc.perform(post("/api/patients")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(mapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById() throws Exception {
        var patientId = UUID.randomUUID();
        var response = new PatientDTOs.PatientResponse(
                patientId, "Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE);

        when(patientService.findById(patientId)).thenReturn(response);

        mvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId.toString()))
                .andExpect(jsonPath("$.fullName").value("Maria Gonzalez"));
    }

    @Test
    void findByIdNotFound() throws Exception {
        var patientId = UUID.randomUUID();

        when(patientService.findById(patientId))
                .thenThrow(new RuntimeException("Patient not found with id: " + patientId));

        mvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isNotFound());

    }

    @Test
    void findAll() throws Exception {
        var patientId1 = UUID.randomUUID();
        var patientId2 = UUID.randomUUID();
        var patients = List.of(
                new PatientDTOs.PatientResponse(patientId1, "Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE),
                new PatientDTOs.PatientResponse(patientId2, "Juan Perez", "juan@mail.com", "555789012", PatientStatus.ACTIVE)
        );

        when(patientService.findAll()).thenReturn(patients);

        mvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Maria Gonzalez"))
                .andExpect(jsonPath("$[1].fullName").value("Juan Perez"));
    }

    @Test
    void findAll_isEmpty() throws Exception {
        var patientId1 = UUID.randomUUID();
        var patientId2 = UUID.randomUUID();
        var patients = List.of(
                new PatientDTOs.PatientResponse(patientId1, "Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE),
                new PatientDTOs.PatientResponse(patientId2, "Juan Perez", "juan@mail.com", "555789012", PatientStatus.ACTIVE)
        );

        mvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void update() throws Exception {
        var patientId = UUID.randomUUID();
        var request = new PatientDTOs.UpdatePatientRequest("Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE);
        var response = new PatientDTOs.PatientResponse(
                patientId,"Maria Gonzalez", "maria@mail.com", "555123456", PatientStatus.ACTIVE);

        when(patientService.update(eq(patientId), any(PatientDTOs.UpdatePatientRequest.class))).thenReturn(response);

        mvc.perform(put("/api/patients/{id}", patientId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Maria Gonzalez"))
                .andExpect(jsonPath("$.phone").value("555123456"));
    }

    @Test
    void updateNotFound() throws Exception {
        var patientId = UUID.randomUUID();
        var request = new PatientDTOs.UpdatePatientRequest(
                "Maria Rodriguez", "maria.rodriguez@mail.com", "555987654", PatientStatus.ACTIVE);

        when(patientService.update(eq(patientId), any(PatientDTOs.UpdatePatientRequest.class)))
                .thenThrow(new RuntimeException("Patient not found with id: " + patientId));

        mvc.perform(put("/api/patients/{id}", patientId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_validationError() throws Exception {
        var invalidRequest = new PatientDTOs.UpdatePatientRequest("","invalid email","111", null);

        mvc.perform(post("/api/patients")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(mapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}