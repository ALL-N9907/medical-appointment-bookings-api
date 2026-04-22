package edu.unimag.medical.api;

import tools.jackson.databind.ObjectMapper;
import edu.unimag.medical.api.dto.PatientDTOs.*;
import edu.unimag.medical.domain.enums.PatientStatus;
import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean PatientService patientService;

    private static final UUID ID = UUID.randomUUID();
    private static final PatientResponse RESPONSE = new PatientResponse(
            ID, "John Doe", "john@email.com", "3001234567", PatientStatus.ACTIVE
    );

    @Test
    void create_shouldReturn201WithCreatedPatient() throws Exception {
        when(patientService.create(any())).thenReturn(RESPONSE);

        mvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(
                                new CreatePatientRequest("John Doe", "john@email.com", "3001234567"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void create_shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(patientService.create(any()))
                .thenThrow(new ConflictException("The email john@email.com already exits"));

        mvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(
                                new CreatePatientRequest("John Doe", "john@email.com", "3001234567"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void create_shouldReturn400WhenEmailIsInvalid() throws Exception {
        mvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(
                                new CreatePatientRequest("John Doe", "not-an-email", "3001234567"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_shouldReturn200WithPatient() throws Exception {
        when(patientService.findById(eq(ID))).thenReturn(RESPONSE);

        mvc.perform(get("/api/patients/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void findById_shouldReturn404WhenPatientNotFound() throws Exception {
        when(patientService.findById(any()))
                .thenThrow(new ResourceNotFoundException("the id " + ID + " was not found"));

        mvc.perform(get("/api/patients/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("the id " + ID + " was not found"));
    }

    @Test
    void findAll_shouldReturn200WithPatientList() throws Exception {
        when(patientService.findAll()).thenReturn(List.of(RESPONSE));

        mvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"));
    }
}