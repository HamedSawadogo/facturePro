package org.facturepro.backoffice.auth.infrastructure.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.facturepro.backoffice.shared.infrastructure.multitenancy.TenantAwarePrincipal;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

/**
 * Filtre JWT — valide le token et injecte le principal dans le SecurityContext.
 * Le principal est TenantAwarePrincipal pour permettre au TenantFilter d'extraire le tenantId.
 */
@Component
public final class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthFilter(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = extractToken(request);

        if (token != null && jwtService.isTokenValid(token)) {
            final TenantAwarePrincipal principal = new TenantAwarePrincipal(
                jwtService.extractUserId(token),
                jwtService.extractTenantId(token),
                jwtService.extractEmail(token)
            );
            final String role = jwtService.extractRole(token);
            final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(final HttpServletRequest request) {
        final String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}