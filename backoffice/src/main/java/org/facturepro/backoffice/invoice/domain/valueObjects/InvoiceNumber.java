package org.facturepro.backoffice.invoice.domain.valueObjects;

import jakarta.persistence.Embeddable;
import java.time.Year;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Value Object numéro de facture.
 * Format : FAC-2025-000001 ou DEV-2025-000001
 */
@Embeddable
public final class InvoiceNumber {

    private final String value;

    protected InvoiceNumber() {
        this.value = null;
    }

    private InvoiceNumber(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static InvoiceNumber of(final String prefix, final int sequence) {
        final String formatted = "%s-%d-%06d".formatted(prefix, Year.now().getValue(), sequence);
        return new InvoiceNumber(formatted);
    }


    public static InvoiceNumber fromString(final String value) {
        return new InvoiceNumber(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof InvoiceNumber that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}