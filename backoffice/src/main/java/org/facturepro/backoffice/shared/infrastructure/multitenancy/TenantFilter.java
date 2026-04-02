package org.facturepro.backoffice.shared.infrastructure.multitenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Extrait le tenantId depuis le principal JWT et l'injecte dans TenantContext.
 * S'exécute après JwtAuthFilter (ordre garanti par SecurityConfig).
 */
@Component
public final class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof TenantAwarePrincipal principal) {
                TenantContext.setCurrentTenant(principal.tenantId());
            }
            filterChain.doFilter(request, response);
        } finally {
            // CRITIQUE : éviter les fuites de contexte entre requêtes dans un thread pool
            TenantContext.clear();
        }
    }
}