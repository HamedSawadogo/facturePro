package org.facturepro.backoffice.auth.domain.enums;

/**
 * Rôles RBAC — définis dès le départ pour Spring Security.
 * ADMIN    : toutes permissions
 * ACCOUNTANT : lecture/écriture factures et paiements
 * VIEWER   : lecture seule
 */
public enum Role {
    ADMIN,
    ACCOUNTANT,
    VIEWER
}