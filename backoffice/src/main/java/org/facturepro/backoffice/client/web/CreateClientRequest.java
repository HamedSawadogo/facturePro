package org.facturepro.backoffice.client.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.facturepro.backoffice.client.domain.enums.ClientType;

public record CreateClientRequest(
    @NotBlank String name,
    String email,
    String phone,
    @NotNull ClientType clientType,
    String address,
    String city,
    String country,
    String taxNumber
) {}