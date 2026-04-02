package org.facturepro.backoffice.payment.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.facturepro.backoffice.payment.domain.enums.PaymentMethod;
import org.facturepro.backoffice.payment.domain.enums.PaymentStatus;
import org.facturepro.backoffice.shared.domain.exceptions.BusinessRuleViolationException;
import org.facturepro.backoffice.shared.domain.valueObjects.Money;
import org.facturepro.backoffice.shared.infrastructure.audit.AuditEntity;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité Paiement — idempotente via idempotencyKey.
 * Un paiement confirmé ne peut pas être modifié, uniquement remboursé.
 */
@Getter
@Entity
@Table(
    name = "payments",
    indexes = {
        @Index(name = "idx_payments_invoice_id", columnList = "invoice_id"),
        @Index(name = "idx_payments_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_payments_idempotency_key", columnList = "idempotency_key", unique = true)
    }
)
public class Payment extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount_value", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "amount_currency", nullable = false))
    })
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "reference")
    private String reference;

    @Column(name = "idempotency_key", unique = true, nullable = false)
    private String idempotencyKey;

    @Column(name = "notes")
    private String notes;

    protected Payment() {}

    public static Payment create(
        final UUID tenantId,
        final UUID invoiceId,
        final Money amount,
        final PaymentMethod method,
        final LocalDate paymentDate,
        final String reference,
        final String idempotencyKey,
        final String notes
    ) {
        final Payment payment = new Payment();
        payment.setTenantId(tenantId);
        payment.invoiceId = invoiceId;
        payment.amount = amount;
        payment.method = method;
        payment.status = PaymentStatus.PENDING;
        payment.paymentDate = paymentDate;
        payment.reference = reference;
        payment.idempotencyKey = idempotencyKey;
        payment.notes = notes;
        return payment;
    }

    // ── Comportements métier ──────────────────────────────────────────────

    public void confirm() {
        if (status != PaymentStatus.PENDING) {
            throw new BusinessRuleViolationException("Seul un paiement en attente peut être confirmé");
        }
        this.status = PaymentStatus.CONFIRMED;
    }

    public void markFailed() {
        if (status != PaymentStatus.PENDING) {
            throw new BusinessRuleViolationException("Impossible de marquer comme échoué: statut " + status);
        }
        this.status = PaymentStatus.FAILED;
    }

    public void refund() {
        if (status != PaymentStatus.CONFIRMED) {
            throw new BusinessRuleViolationException("Seul un paiement confirmé peut être remboursé");
        }
        this.status = PaymentStatus.REFUNDED;
    }
}