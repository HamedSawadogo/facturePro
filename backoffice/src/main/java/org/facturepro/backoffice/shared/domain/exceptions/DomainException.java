package org.facturepro.backoffice.shared.domain.exceptions;

/**
 * Exception racine du domaine — toujours rattachée à une règle métier.
 */
public class DomainException extends RuntimeException {

    public DomainException(final String message) {
        super(message);
    }

    public DomainException(final String message, final Throwable cause) {
        super(message, cause);
    }
}