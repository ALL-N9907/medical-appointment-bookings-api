package edu.unimag.medical.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class DoctorDTOs {
    public  record CreateDoctorRequest(@NotBlank String fullName, @NotNull UUID specialtyId) implements Serializable{}
    public  record UpdateDoctorRequest(@NotBlank String fullName, boolean active, @NotNull UUID specialtyId ) implements Serializable{}
    public record DoctorResponse(UUID id, String fullName, boolean active, String specialtyName) implements Serializable{}
}
