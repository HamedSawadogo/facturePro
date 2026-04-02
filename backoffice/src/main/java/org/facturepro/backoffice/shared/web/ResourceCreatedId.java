package org.facturepro.backoffice.shared.web;

import java.util.UUID;

/**
 * Réponse standard pour Create/Update — retourne uniquement l'ID.
 * Principe : les mutations ne retournent que l'ID pour forcer le client à faire un GET.
 */
public record ResourceCreatedId(UUID id) {

    public static ResourceCreatedId of(final UUID id) {
        return new ResourceCreatedId(id);
    }
}