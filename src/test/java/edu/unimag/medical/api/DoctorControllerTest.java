package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.DoctorDTOs;
import edu.unimag.medical.service.DoctorService;
import edu.unimag.medical.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    private MockMvc mvc;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(doctorController).build();
    }

    @Test
    void create() throws Exception {
        var doctorId = UUID.randomUUID();
        var specialtyId = UUID.randomUUID();
        var request = new DoctorDTOs.CreateDoctorRequest("John Doe", specialtyId);
        var response = new DoctorDTOs.DoctorResponse(
                doctorId, "John Doe", true, "Cardiology");

        when(doctorService.create(any(DoctorDTOs.CreateDoctorRequest.class))).thenReturn(response);

        String JsonRequest = "{\"fullName\":\"John Doe\",\"specialtyId\":\"" + specialtyId.toString() + "\"}";

        mvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", org.hamcrest.Matchers.endsWith("/api/doctors/" + doctorId)))
                .andExpect(jsonPath("$.id").value(doctorId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void createAlreadyExists() throws Exception {
        var specialtyId = UUID.randomUUID();

        when(doctorService.create(any(DoctorDTOs.CreateDoctorRequest.class)))
                .thenThrow(new RuntimeException("Doctor already exists with fullName: John Doe"));

        String jsonRequest = "{\"fullName\":\"John Doe\",\"specialtyId\":\"" + specialtyId.toString() + "\"}";

        mvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void findById() throws Exception {
        var doctorId = UUID.randomUUID();
        var response = new DoctorDTOs.DoctorResponse(
                doctorId, "John Doe", true, "Cardiology");

        when(doctorService.findById(doctorId)).thenReturn(response);

        mvc.perform(get("/api/doctors/{id}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void findByIdNotFound() throws Exception {
        var doctorId = UUID.randomUUID();

        when(doctorService.findById(doctorId))
                .thenThrow(new RuntimeException("Doctor not found with id: " + doctorId));

        mvc.perform(get("/api/doctors/{id}", doctorId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll() throws Exception {
        var doctorId1 = UUID.randomUUID();
        var doctorId2 = UUID.randomUUID();
        var doctors = List.of(
                new DoctorDTOs.DoctorResponse(doctorId1, "John Doe", true, "Cardiology"),
                new DoctorDTOs.DoctorResponse(doctorId2, "Jane Smith", true, "Neurology")
        );
        when(doctorService.findAll()).thenReturn(doctors);

        mvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[1].fullName").value("Jane Smith"));
    }

    @Test
    void findAll_isEmpty() throws Exception {
        when(doctorService.findAll()).thenReturn(List.of());

        mvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updateDoctor() throws Exception {
        var doctorId = UUID.randomUUID();
        var specialtyId = UUID.randomUUID();
        var response = new DoctorDTOs.DoctorResponse(
                doctorId, "Jane Smith", false, "Cardiology");

        when(doctorService.update(eq(doctorId), any(DoctorDTOs.UpdateDoctorRequest.class))).thenReturn(response);

        String jsonRequest =
                "{\"fullName\":\"Jane Smith\",\"active\":false,\"specialtyId\":\"" + specialtyId.toString() + "\"}";

        mvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Smith"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void updateDoctorNotFound() throws Exception {
        var doctorId = UUID.randomUUID();
        var specialtyId = UUID.randomUUID();

        when(doctorService.update(eq(doctorId), any(DoctorDTOs.UpdateDoctorRequest.class)))
                .thenThrow(new RuntimeException("Doctor not found with id: " + doctorId));

        String jsonRequest =
                "{\"fullName\":\"Jane Smith\",\"active\":false,\"specialtyId\":\"" + specialtyId.toString() + "\"}";

        mvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAlreadyExists() throws Exception {
        var doctorId = UUID.randomUUID();
        var specialtyId = UUID.randomUUID();

        when(doctorService.update(eq(doctorId), any(DoctorDTOs.UpdateDoctorRequest.class)))
                .thenThrow(new RuntimeException("Doctor already exists with fullName: Jane Smith"));

        String jsonRequest = "{\"fullName\":\"Jane Smith\",\"active\":false,\"specialtyId\":\"" + specialtyId.toString() + "\"}";

        mvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void updateDoctor_validationError() throws Exception {
        var doctorId = UUID.randomUUID();
        String invalidJson = "{\"fullName\":\"\",\"active\":false,\"specialtyId\":null}";

        mvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}