package edu.unimag.medical.domain.dto;

import edu.unimag.medical.domain.enums.OfficeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class OfficeDTOs {
    public record CreateOfficeRequest(@NotNull Integer number, @NotBlank String location) implements Serializable{}
    public record UpdateOfficeRequest(@NotNull Integer number, @NotBlank String location, @NotNull OfficeStatus status) implements Serializable{}
    public record OfficeResponse(UUID id, Integer number, String location, OfficeStatus status ) implements Serializable{}
}
