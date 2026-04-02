package org.facturepro.backoffice.auth.application.commands;

import org.facturepro.backoffice.auth.domain.enums.Role;

import java.util.UUID;

/**
 * Commande d'enregistrement d'un utilisateur.
 * Record immuable — 1 Use Case = 1 Commande.
 */
public record RegisterUserCommand(
    UUID tenantId,
    String email,
    String password,
    String firstName,
    String lastName,
    Role role
) {}
