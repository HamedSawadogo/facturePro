package org.facturepro.backoffice.auth.infrastructure.adapters;

import org.facturepro.backoffice.auth.domain.entities.User;
import org.facturepro.backoffice.auth.domain.repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation JPA du port UserRepository.
 * Étend à la fois JpaRepository (Spring Data) et UserRepository (port domaine).
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepository {

    Optional<User> findByEmailValueAndTenantId(String email, UUID tenantId);

    Optional<User> findByEmailValue(String email);

    boolean existsByEmailValueAndTenantId(String email, UUID tenantId);

    // Mapping des méthodes du port vers les conventions Spring Data
    default Optional<User> findByEmailAndTenantId(String email, UUID tenantId) {
        return findByEmailValueAndTenantId(email, tenantId);
    }

    default Optional<User> findByEmail(String email) {
        return findByEmailValue(email);
    }

    default boolean existsByEmailAndTenantId(String email, UUID tenantId) {
        return existsByEmailValueAndTenantId(email, tenantId);
    }

    default Optional<User> findByIdAndTenantId(UUID id, UUID tenantId) {
        return findById(id).filter(u -> u.getTenantId().equals(tenantId));
    }
}