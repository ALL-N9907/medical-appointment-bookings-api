package edu.unimag.medical.security.dto;

import edu.unimag.medical.security.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class AuthDtos {

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ){}

    public record AuthResponse(
            String accessToken,
            String tokenType,
            long expiresInSeconds,
            String email,
            Set<Role> roles
    ) {}

    public record CreateStaffRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotEmpty Set<Role> roles
    ){}

}
