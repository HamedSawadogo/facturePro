package org.facturepro.backoffice.shared.domain.valueObjects;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object numéro de téléphone — format international E.164 (+226XXXXXXXX).
 */
@Embeddable
public final class PhoneNumber {

    // Format E.164 : + suivi de 7 à 15 chiffres
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");

    private final String value;

    protected PhoneNumber() {
        this.value = null;
    }

    private PhoneNumber(final String value) {
        Objects.requireNonNull(value, "Le numéro de téléphone ne peut pas être null");
        final String normalized = value.trim().replaceAll("\\s+", "");
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                "Format téléphone invalide (attendu E.164 ex: +22670123456): " + value
            );
        }
        this.value = normalized;
    }

    public static PhoneNumber of(final String value) {
        return new PhoneNumber(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber that)) return false;
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