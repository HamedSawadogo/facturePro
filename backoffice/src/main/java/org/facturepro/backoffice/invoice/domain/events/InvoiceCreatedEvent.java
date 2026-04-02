package org.facturepro.backoffice.invoice.domain.events;

import org.facturepro.backoffice.invoice.domain.enums.InvoiceType;
import org.facturepro.backoffice.shared.domain.events.DomainEvent;

import java.util.UUID;

/**
 * Événement publié après COMMIT de la création d'une facture.
 * Consommé par : InvoiceEventListener → génération PDF + notification email.
 */
public final class InvoiceCreatedEvent extends DomainEvent {

    private final UUID invoiceId;
    private final String invoiceNumber;
    private final UUID clientId;
    private final String clientName;
    private final InvoiceType invoiceType;

    public InvoiceCreatedEvent(
        final UUID tenantId,
        final UUID invoiceId,
        final String invoiceNumber,
        final UUID clientId,
        final String clientName,
        final InvoiceType invoiceType
    ) {
        super(tenantId);
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.clientId = clientId;
        this.clientName = clientName;
        this.invoiceType = invoiceType;
    }

    public UUID getInvoiceId() { return invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public UUID getClientId() { return clientId; }
    public String getClientName() { return clientName; }
    public InvoiceType getInvoiceType() { return invoiceType; }
}