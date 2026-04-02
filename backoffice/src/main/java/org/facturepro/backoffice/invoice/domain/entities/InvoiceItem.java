package org.facturepro.backoffice.invoice.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.facturepro.backoffice.shared.domain.valueObjects.Money;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Ligne d'une facture.
 * Entité fille — cycle de vie lié à Invoice.
 */
@Getter
@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_price_amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency", nullable = false))
    })
    private Money unitPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    protected InvoiceItem() {}

    private InvoiceItem(
        final Invoice invoice,
        final String description,
        final BigDecimal quantity,
        final Money unitPrice,
        final BigDecimal taxRate
    ) {
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate != null ? taxRate : BigDecimal.ZERO;
    }

    public static InvoiceItem create(
        final Invoice invoice,
        final String description,
        final BigDecimal quantity,
        final Money unitPrice,
        final BigDecimal taxRate
    ) {
        return new InvoiceItem(invoice, description, quantity, unitPrice, taxRate);
    }

    /** Sous-total HT */
    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }

    /** Montant TVA */
    public Money taxAmount() {
        return subtotal().multiply(taxRate.divide(BigDecimal.valueOf(100)));
    }

    /** Total TTC */
    public Money total() {
        return subtotal().add(taxAmount());
    }
}