package org.facturepro.backoffice.shared.infrastructure.multitenancy;

import java.util.UUID;

/**
 * Holder ThreadLocal du tenant courant.
 * Alimenté par TenantFilter à chaque requête HTTP.
 * IMPORTANT : toujours appeler clear() en fin de requête (fait dans le filter).
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setCurrentTenant(final UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static UUID getCurrentTenant() {
        final UUID tenant = CURRENT_TENANT.get();
        if (tenant == null) {
            throw new IllegalStateException("Aucun tenant dans le contexte courant");
        }
        return tenant;
    }

    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}