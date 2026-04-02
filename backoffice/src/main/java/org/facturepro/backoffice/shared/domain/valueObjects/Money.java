package org.facturepro.backoffice.shared.domain.valueObjects;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Value Object monétaire.
 * Seule façon d'exprimer un montant dans le domaine — jamais de BigDecimal nu.
 * Utilise FCFA (XOF) par défaut pour le marché africain.
 */
@Embeddable
public final class Money {

    public static final String DEFAULT_CURRENCY = "XOF";
    private static final int SCALE = 2;

    private final BigDecimal amount;
    private final String currency;

    // Constructeur JPA
    protected Money() {
        this.amount = BigDecimal.ZERO;
        this.currency = DEFAULT_CURRENCY;
    }

    private Money(final BigDecimal amount, final String currency) {
        Objects.requireNonNull(amount, "Le montant ne peut pas être null");
        Objects.requireNonNull(currency, "La devise ne peut pas être null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Un montant monétaire ne peut pas être négatif");
        }
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public static Money of(final BigDecimal amount, final String currency) {
        return new Money(amount, currency);
    }

    public static Money of(final double amount, final String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money ofXof(final BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }

    public Money add(final Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(final Money other) {
        assertSameCurrency(other);
        final BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La soustraction produit un montant négatif");
        }
        return new Money(result, this.currency);
    }

    public Money multiply(final BigDecimal factor) {
        return new Money(this.amount.multiply(factor), this.currency);
    }

    public boolean isGreaterThan(final Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    private void assertSameCurrency(final Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Impossible d'opérer sur des devises différentes: %s vs %s".formatted(this.currency, other.currency)
            );
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(amount.toPlainString(), currency);
    }
}