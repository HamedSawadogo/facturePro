package org.facturepro.backoffice.invoice.application.commands;

import org.facturepro.backoffice.invoice.domain.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceCommand(
    UUID tenantId,
    InvoiceType type,
    UUID clientId,
    String clientName,
    LocalDate issueDate,
    LocalDate dueDate,
    List<ItemCommand> items,
    String notes,
    String paymentTerms,
    String idempotencyKey
) {
    public record ItemCommand(
        String description,
        BigDecimal quantity,
        BigDecimal unitPriceAmount,
        String currency,
        BigDecimal taxRate
    ) {}
}