package org.facturepro.backoffice.shared.infrastructure.multitenancy;

import java.util.UUID;

/**
 * Record portant les informations du principal JWT enrichi du tenantId.
 */
public record TenantAwarePrincipal(UUID userId, UUID tenantId, String email) { }