package org.facturepro.backoffice.auth.infrastructure.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.facturepro.backoffice.auth.domain.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Service JWT — génération et validation des tokens.
 * Claims personnalisés : userId, tenantId, email, role.
 */
@Service
public final class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtService(
        @Value("${app.jwt.secret}") final String secret,
        @Value("${app.jwt.expiration-ms}") final long expirationMs,
        @Value("${app.jwt.refresh-expiration-ms}") final long refreshExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(final User user) {
        return buildToken(user, expirationMs, "access");
    }

    public String generateRefreshToken(final User user) {
        return buildToken(user, refreshExpirationMs, "refresh");
    }

    private String buildToken(final User user, final long ttl, final String type) {
        final Date now = new Date();
        return Jwts.builder()
            .subject(user.getEmail().getValue())
            .claims(Map.of(
                "userId", user.getId().toString(),
                "tenantId", user.getTenantId().toString(),
                "role", user.getRole().name(),
                "type", type
            ))
            .issuedAt(now)
            .expiration(new Date(now.getTime() + ttl))
            .signWith(secretKey)
            .compact();
    }

    public Claims extractClaims(final String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenValid(final String token) {
        try {
            final Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date())
                && "access".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractEmail(final String token) {
        return extractClaims(token).getSubject();
    }

    public UUID extractUserId(final String token) {
        return UUID.fromString(extractClaims(token).get("userId", String.class));
    }

    public UUID extractTenantId(final String token) {
        return UUID.fromString(extractClaims(token).get("tenantId", String.class));
    }

    public String extractRole(final String token) {
        return extractClaims(token).get("role", String.class);
    }
}