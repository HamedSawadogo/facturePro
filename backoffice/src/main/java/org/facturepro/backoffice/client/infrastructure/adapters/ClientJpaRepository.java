package org.facturepro.backoffice.client.infrastructure.adapters;

import org.facturepro.backoffice.client.domain.entities.Client;
import org.facturepro.backoffice.client.domain.repositories.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientJpaRepository extends JpaRepository<Client, UUID>, ClientRepository {

    Optional<Client> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Client> findAllByTenantIdAndActive(UUID tenantId, boolean active, Pageable pageable);

    @Query("""
        SELECT c FROM Client c
        WHERE c.tenantId = :tenantId
        AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(c.email.value) LIKE LOWER(CONCAT('%', :query, '%')))
        """)
    Page<Client> searchByTenantId(
        @Param("tenantId") UUID tenantId,
        @Param("query") String query,
        Pageable pageable
    );

    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
}