package org.facturepro.backoffice.auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.facturepro.backoffice.auth.domain.enums.Role;

import java.util.UUID;

public record RegisterRequest(
    @NotNull UUID tenantId,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull Role role
) {}