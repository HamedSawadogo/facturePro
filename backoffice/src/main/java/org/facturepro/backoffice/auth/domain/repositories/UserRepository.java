package org.facturepro.backoffice.auth.domain.repositories;

import org.facturepro.backoffice.auth.domain.entities.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port du domaine — contrat d'accès aux utilisateurs.
 * Implémenté par l'infrastructure (UserJpaRepository).
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);
}
