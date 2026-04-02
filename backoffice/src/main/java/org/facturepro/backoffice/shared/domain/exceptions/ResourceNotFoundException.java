package org.facturepro.backoffice.shared.domain.exceptions;

import java.util.UUID;

/**
 * Ressource introuvable — mappée en HTTP 404.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(final String resourceName, final UUID id) {
        super("%s introuvable avec l'identifiant: %s".formatted(resourceName, id));
    }

    public ResourceNotFoundException(final String message) {
        super(message);
    }
}