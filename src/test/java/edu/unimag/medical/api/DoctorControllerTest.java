package edu.unimag.medical.api;

import tools.jackson.databind.ObjectMapper;
import edu.unimag.medical.api.dto.DoctorDTOs.*;
import edu.unimag.medical.exception.ResourceNotFoundException;
import edu.unimag.medical.service.DoctorService;
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

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean DoctorService doctorService;

    private static final UUID ID           = UUID.randomUUID();
    private static final UUID SPECIALTY_ID = UUID.randomUUID();
    private static final DoctorResponse RESPONSE = new DoctorResponse(
            ID, "Dr. House", true, "Cardiology"
    );

    @Test
    void create_shouldReturn201WithCreatedDoctor() throws Exception {
        when(doctorService.create(any())).thenReturn(RESPONSE);

        mvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateDoctorRequest("Dr. House", SPECIALTY_ID))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.fullName").value("Dr. House"))
                .andExpect(jsonPath("$.specialtyName").value("Cardiology"));
    }

    @Test
    void create_shouldReturn400WhenFullNameIsBlank() throws Exception {
        mvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateDoctorRequest("", SPECIALTY_ID))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturn200WithUpdatedDoctor() throws Exception {
        var updated = new DoctorResponse(ID, "Dr. House Updated", true, "Neurology");

        when(doctorService.update(eq(ID), any())).thenReturn(updated);

        mvc.perform(put("/api/doctors/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateDoctorRequest("Dr. House Updated", true, SPECIALTY_ID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Dr. House Updated"))
                .andExpect(jsonPath("$.specialtyName").value("Neurology"));
    }

    @Test
    void findById_shouldReturn200WithDoctor() throws Exception {
        when(doctorService.findById(eq(ID))).thenReturn(RESPONSE);

        mvc.perform(get("/api/doctors/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.fullName").value("Dr. House"));
    }

    @Test
    void findById_shouldReturn404WhenDoctorNotFound() throws Exception {
        when(doctorService.findById(any()))
                .thenThrow(new ResourceNotFoundException("the id " + ID + " was not found"));

        mvc.perform(get("/api/doctors/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("the id " + ID + " was not found"));
    }

    @Test
    void findAll_shouldReturn200WithDoctorList() throws Exception {
        when(doctorService.findAll()).thenReturn(List.of(RESPONSE));

        mvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Dr. House"));
    }
}