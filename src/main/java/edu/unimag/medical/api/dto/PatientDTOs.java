package edu.unimag.medical.api.dto;

import edu.unimag.medical.domain.enums.PatientStatus;
import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PatientDTOs {

    public record CreatePatientRequest(@NotBlank String fullName,@NotBlank @Email String email,@NotBlank String phone) implements Serializable{}
    public record UpdatePatientRequest(@NotBlank String fullName, @NotBlank @Email String email, @NotBlank String phone,@NotNull PatientStatus status) implements Serializable{}
    public record PatientResponse(UUID id, String fullName, String email, String phone, PatientStatus status) implements Serializable{}

}
