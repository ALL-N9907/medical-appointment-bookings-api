package edu.unimag.medical.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class AppointmentTypeDTO {

    public record CreateAppointmentTypeRequest(@NotBlank String name, @NotNull Integer durationMinutes) implements Serializable{}
    public record AppointmentTypeResponse(UUID id, String name, Integer durationMinutes) implements Serializable{}

}
