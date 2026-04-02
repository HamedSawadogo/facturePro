package org.facturepro.backoffice.client.application.commands;

import org.facturepro.backoffice.client.domain.entities.Client;
import org.facturepro.backoffice.client.domain.repositories.ClientRepository;
import org.facturepro.backoffice.shared.domain.exceptions.ResourceNotFoundException;
import org.facturepro.backoffice.shared.domain.valueObjects.Email;
import org.facturepro.backoffice.shared.domain.valueObjects.PhoneNumber;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public  class UpdateClientUseCase {

    private final ClientRepository clientRepository;

    public UpdateClientUseCase(final ClientRepository clientRepository) {
        this.clientRepository = Objects.requireNonNull(clientRepository);
    }

    public ResourceCreatedId execute(final UpdateClientCommand command) {
        final Client client = clientRepository.findByIdAndTenantId(command.id(), command.tenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Client", command.id()));

        client.update(
            command.name(),
            command.email() != null ? Email.of(command.email()) : null,
            command.phone() != null ? PhoneNumber.of(command.phone()) : null,
            command.address(),
            command.city(),
            command.country(),
            command.taxNumber()
        );

        return ResourceCreatedId.of(client.getId());
    }
}