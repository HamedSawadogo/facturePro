package org.facturepro.backoffice.invoice.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.facturepro.backoffice.invoice.domain.enums.InvoiceStatus;
import org.facturepro.backoffice.invoice.domain.enums.InvoiceType;
import org.facturepro.backoffice.invoice.domain.valueObjects.InvoiceNumber;
import org.facturepro.backoffice.shared.domain.exceptions.BusinessRuleViolationException;
import org.facturepro.backoffice.shared.domain.valueObjects.Money;
import org.facturepro.backoffice.shared.infrastructure.audit.AuditEntity;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Agrégat racine Invoice — toute modification passe par ses méthodes.
 * Encapsule le cycle de vie : DRAFT → SENT → PAID | OVERDUE | CANCELLED.
 */
@Getter
@Entity
@Table(
    name = "invoices",
    indexes = {
        @Index(name = "idx_invoices_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_invoices_client_id", columnList = "client_id"),
        @Index(name = "idx_invoices_status", columnList = "status"),
        @Index(name = "idx_invoices_due_date", columnList = "due_date")
    }
)
public class Invoice extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "invoice_number", unique = true))
    private InvoiceNumber invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InvoiceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_ht_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "total_ht_currency"))
    })
    private Money totalHt;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_ttc_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "total_ttc_currency"))
    })
    private Money totalTtc;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount_paid_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "amount_paid_currency"))
    })
    private Money amountPaid;

    @Column(name = "notes")
    private String notes;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    // Cascade ALL : les items vivent et meurent avec la facture
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<InvoiceItem> items = new HashSet<>();

    protected Invoice() {}

    public static Invoice create(
        final UUID tenantId,
        final InvoiceNumber invoiceNumber,
        final InvoiceType type,
        final UUID clientId,
        final String clientName,
        final LocalDate issueDate,
        final LocalDate dueDate,
        final String notes,
        final String paymentTerms,
        final String idempotencyKey
    ) {
        final Invoice invoice = new Invoice();
        invoice.setTenantId(tenantId);
        invoice.invoiceNumber = invoiceNumber;
        invoice.type = type;
        invoice.status = InvoiceStatus.DRAFT;
        invoice.clientId = clientId;
        invoice.clientName = clientName;
        invoice.issueDate = issueDate;
        invoice.dueDate = dueDate;
        invoice.notes = notes;
        invoice.paymentTerms = paymentTerms;
        invoice.idempotencyKey = idempotencyKey;
        invoice.totalHt = Money.zero();
        invoice.totalTtc = Money.zero();
        invoice.amountPaid = Money.zero();
        return invoice;
    }

    // ── Comportements métier ──────────────────────────────────────────────

    public void addItem(final InvoiceItem item) {
        assertDraft();
        items.add(item);
        recalculate();
    }

    public void send() {
        if (status != InvoiceStatus.DRAFT) {
            throw new BusinessRuleViolationException(
                "Impossible d'envoyer une facture avec le statut: " + status
            );
        }
        if (items.isEmpty()) {
            throw new BusinessRuleViolationException("Impossible d'envoyer une facture sans lignes");
        }
        this.status = InvoiceStatus.SENT;
    }

    /**
     * Enregistre un paiement partiel ou total.
     * Retourne true si la facture est entièrement réglée.
     */
    public boolean recordPayment(final Money amount) {
        if (status == InvoiceStatus.PAID || status == InvoiceStatus.CANCELLED) {
            throw new BusinessRuleViolationException(
                "Impossible d'enregistrer un paiement sur une facture " + status
            );
        }
        this.amountPaid = this.amountPaid.add(amount);
        if (this.amountPaid.getAmount().compareTo(this.totalTtc.getAmount()) >= 0) {
            this.status = InvoiceStatus.PAID;
            return true;
        }
        this.status = InvoiceStatus.PARTIALLY_PAID;
        return false;
    }

    public void markOverdue() {
        if (status == InvoiceStatus.SENT || status == InvoiceStatus.PARTIALLY_PAID) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }

    public void cancel() {
        if (status == InvoiceStatus.PAID) {
            throw new BusinessRuleViolationException("Impossible d'annuler une facture déjà payée");
        }
        this.status = InvoiceStatus.CANCELLED;
    }

    /** Facture DEVIS → convertit en FACTURE */
    public void convertToInvoice(final InvoiceNumber newNumber) {
        if (type != InvoiceType.QUOTE) {
            throw new BusinessRuleViolationException("Seul un devis peut être converti en facture");
        }
        this.type = InvoiceType.INVOICE;
        this.invoiceNumber = newNumber;
        this.status = InvoiceStatus.DRAFT;
        this.issueDate = LocalDate.now();
    }

    public Money getRemainingAmount() {
        return totalTtc.subtract(amountPaid);
    }

    private void assertDraft() {
        if (status != InvoiceStatus.DRAFT) {
            throw new BusinessRuleViolationException(
                "Modification impossible — la facture n'est plus en brouillon"
            );
        }
    }

    private void recalculate() {
        this.totalHt = items.stream()
            .map(InvoiceItem::subtotal)
            .reduce(Money.zero(), Money::add);
        this.totalTtc = items.stream()
            .map(InvoiceItem::total)
            .reduce(Money.zero(), Money::add);
    }
}