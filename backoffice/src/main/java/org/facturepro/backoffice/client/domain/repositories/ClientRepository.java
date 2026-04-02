package org.facturepro.backoffice.client.domain.repositories;

import org.facturepro.backoffice.client.domain.entities.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Client> findAllByTenantIdAndActive(UUID tenantId, boolean active, Pageable pageable);

    Page<Client> searchByTenantId(UUID tenantId, String query, Pageable pageable);

    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
}