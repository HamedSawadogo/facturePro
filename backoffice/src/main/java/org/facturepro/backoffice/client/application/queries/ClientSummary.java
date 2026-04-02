package org.facturepro.backoffice.client.application.queries;

import org.facturepro.backoffice.client.domain.enums.ClientType;

import java.util.UUID;

/**
 * Projection légère pour les listes — jamais l'entité complète inutilement.
 */
public record ClientSummary(
    UUID id,
    String name,
    String email,
    String phone,
    ClientType clientType,
    String city,
    String country,
    boolean active
) {}