package org.facturepro.backoffice.client.application.commands;

import org.facturepro.backoffice.client.domain.enums.ClientType;

import java.util.UUID;

public record CreateClientCommand(
    UUID tenantId,
    String name,
    String email,
    String phone,
    ClientType clientType,
    String address,
    String city,
    String country,
    String taxNumber
) {}