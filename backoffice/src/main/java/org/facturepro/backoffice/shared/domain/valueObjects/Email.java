package org.facturepro.backoffice.shared.domain.valueObjects;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object Email — validation à la construction, jamais de String nu dans le domaine.
 */
@Embeddable
public final class Email {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private final String value;

    protected Email() {
        this.value = null;
    }

    private Email(final String value) {
        Objects.requireNonNull(value, "L'email ne peut pas être null");
        final String trimmed = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Format email invalide: " + value);
        }
        this.value = trimmed;
    }

    public static Email of(final String value) {
        return new Email(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
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