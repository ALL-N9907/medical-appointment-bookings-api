package edu.unimag.medical.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class SpecialtyDTOs {
    public record CreateSpecialtyRequest(@NotBlank String name) implements Serializable{}
    public record SpecialtyResponse(UUID id, String name) implements Serializable{}

}
