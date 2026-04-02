package org.facturepro.backoffice.client.application.commands;

import java.util.UUID;

public record UpdateClientCommand(
    UUID id,
    UUID tenantId,
    String name,
    String email,
    String phone,
    String address,
    String city,
    String country,
    String taxNumber
) {}