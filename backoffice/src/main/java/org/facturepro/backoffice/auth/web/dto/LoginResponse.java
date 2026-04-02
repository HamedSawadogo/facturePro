package org.facturepro.backoffice.auth.web.dto;

import java.util.UUID;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    UUID userId,
    UUID tenantId,
    String email,
    String fullName,
    String role
) {}