package org.facturepro.backoffice.client.application.commands;

import org.facturepro.backoffice.client.domain.entities.Client;
import org.facturepro.backoffice.client.domain.repositories.ClientRepository;
import org.facturepro.backoffice.shared.domain.valueObjects.Email;
import org.facturepro.backoffice.shared.domain.valueObjects.PhoneNumber;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class CreateClientUseCase {

    private final ClientRepository clientRepository;

    public CreateClientUseCase(final ClientRepository clientRepository) {
        this.clientRepository = Objects.requireNonNull(clientRepository);
    }

    public ResourceCreatedId execute(final CreateClientCommand command) {
        final Client client = Client.create(
            command.tenantId(),
            command.name(),
            command.email() != null ? Email.of(command.email()) : null,
            command.phone() != null ? PhoneNumber.of(command.phone()) : null,
            command.clientType(),
            command.address(),
            command.city(),
            command.country(),
            command.taxNumber()
        );
        final Client saved = clientRepository.save(client);
        return ResourceCreatedId.of(saved.getId());
    }
}