package org.facturepro.backoffice.client.application.queries;

import java.util.UUID;

public record ListClientsQuery(
    UUID tenantId,
    String search,
    int page,
    int size
) {}