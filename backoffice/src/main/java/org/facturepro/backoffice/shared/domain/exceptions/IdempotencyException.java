package org.facturepro.backoffice.shared.domain.exceptions;

/**
 * Détection d'une requête dupliquée via idempotency key — mappée en HTTP 409 Conflict.
 */
public class IdempotencyException extends DomainException {

    public IdempotencyException(final String idempotencyKey) {
        super("Opération déjà traitée pour la clé d'idempotence: " + idempotencyKey);
    }
}