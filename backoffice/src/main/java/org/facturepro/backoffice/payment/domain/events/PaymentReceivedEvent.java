package org.facturepro.backoffice.payment.domain.events;

import org.facturepro.backoffice.shared.domain.events.DomainEvent;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Événement publié après confirmation d'un paiement.
 * Consommé par le module invoice pour mettre à jour le statut.
 */
public final class PaymentReceivedEvent extends DomainEvent {

    private final UUID paymentId;
    private final UUID invoiceId;
    private final BigDecimal amount;
    private final String currency;

    public PaymentReceivedEvent(
        final UUID tenantId,
        final UUID paymentId,
        final UUID invoiceId,
        final BigDecimal amount,
        final String currency
    ) {
        super(tenantId);
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID getPaymentId() { return paymentId; }
    public UUID getInvoiceId() { return invoiceId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
}