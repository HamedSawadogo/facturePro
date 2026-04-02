package org.facturepro.backoffice.client.web;

import jakarta.validation.constraints.NotBlank;

public record UpdateClientRequest(
    @NotBlank String name,
    String email,
    String phone,
    String address,
    String city,
    String country,
    String taxNumber
) {}