package org.facturepro.backoffice.invoice.application.queries;

import org.facturepro.backoffice.invoice.domain.enums.InvoiceStatus;

import java.util.UUID;

public record ListInvoicesQuery(
    UUID tenantId,
    InvoiceStatus status,
    UUID clientId,
    int page,
    int size
) {}