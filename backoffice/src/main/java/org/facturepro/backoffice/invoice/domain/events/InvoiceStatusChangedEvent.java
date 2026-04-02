package org.facturepro.backoffice.invoice.domain.events;

import org.facturepro.backoffice.invoice.domain.enums.InvoiceStatus;
import org.facturepro.backoffice.shared.domain.events.DomainEvent;

import java.util.UUID;

/**
 * Événement de changement de statut — consommé par le module notification.
 */
public final class InvoiceStatusChangedEvent extends DomainEvent {

    private final UUID invoiceId;
    private final String invoiceNumber;
    private final InvoiceStatus previousStatus;
    private final InvoiceStatus newStatus;
    private final UUID clientId;

    public InvoiceStatusChangedEvent(
        final UUID tenantId,
        final UUID invoiceId,
        final String invoiceNumber,
        final InvoiceStatus previousStatus,
        final InvoiceStatus newStatus,
        final UUID clientId
    ) {
        super(tenantId);
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.clientId = clientId;
    }

    public UUID getInvoiceId() { return invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public InvoiceStatus getPreviousStatus() { return previousStatus; }
    public InvoiceStatus getNewStatus() { return newStatus; }
    public UUID getClientId() { return clientId; }
}