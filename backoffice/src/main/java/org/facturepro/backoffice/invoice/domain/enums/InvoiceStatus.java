package org.facturepro.backoffice.invoice.domain.enums;

/**
 * Cycle de vie d'une facture FacturePro.
 * DRAFT → SENT → PARTIALLY_PAID → PAID | OVERDUE | CANCELLED
 */
public enum InvoiceStatus {
    DRAFT,
    SENT,
    PARTIALLY_PAID,
    PAID,
    OVERDUE,
    CANCELLED
}