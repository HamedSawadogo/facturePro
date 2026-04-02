package org.facturepro.backoffice.invoice.application.queries;

import org.facturepro.backoffice.invoice.domain.enums.InvoiceStatus;
import org.facturepro.backoffice.invoice.domain.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Projection légère pour la liste des factures.
 */
public record InvoiceSummary(
    UUID id,
    String invoiceNumber,
    InvoiceType type,
    InvoiceStatus status,
    String clientName,
    LocalDate issueDate,
    LocalDate dueDate,
    BigDecimal totalTtcAmount,
    String currency,
    BigDecimal amountPaidAmount
) {}