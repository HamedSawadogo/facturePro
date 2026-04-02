package org.facturepro.backoffice.client.application.queries;

import org.facturepro.backoffice.client.domain.entities.Client;
import org.facturepro.backoffice.client.domain.repositories.ClientRepository;
import org.facturepro.backoffice.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public  class GetClientUseCase {

    private final ClientRepository clientRepository;

    public GetClientUseCase(final ClientRepository clientRepository) {
        this.clientRepository = Objects.requireNonNull(clientRepository);
    }

    public ClientSummary execute(final UUID clientId, final UUID tenantId) {
        final Client c = clientRepository.findByIdAndTenantId(clientId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        return new ClientSummary(
            c.getId(),
            c.getName(),
            c.getEmail() != null ? c.getEmail().getValue() : null,
            c.getPhone() != null ? c.getPhone().getValue() : null,
            c.getClientType(),
            c.getCity(),
            c.getCountry(),
            c.isActive()
        );
    }
}