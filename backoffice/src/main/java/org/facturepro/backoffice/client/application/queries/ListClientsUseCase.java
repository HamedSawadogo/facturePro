package org.facturepro.backoffice.client.application.queries;

import org.facturepro.backoffice.client.domain.repositories.ClientRepository;
import org.facturepro.backoffice.shared.web.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ListClientsUseCase {

    private final ClientRepository clientRepository;

    public ListClientsUseCase(final ClientRepository clientRepository) {
        this.clientRepository = Objects.requireNonNull(clientRepository);
    }

    public PageResponse<ClientSummary> execute(final ListClientsQuery query) {
        final PageRequest pageable = PageRequest.of(
            query.page(), query.size(),
            Sort.by(Sort.Direction.ASC, "name")
        );

        final var page = StringUtils.hasText(query.search())
            ? clientRepository.searchByTenantId(query.tenantId(), query.search(), pageable)
            : clientRepository.findAllByTenantIdAndActive(query.tenantId(), true, pageable);

        return PageResponse.of(page.map(c -> new ClientSummary(
            c.getId(),
            c.getName(),
            c.getEmail() != null ? c.getEmail().getValue() : null,
            c.getPhone() != null ? c.getPhone().getValue() : null,
            c.getClientType(),
            c.getCity(),
            c.getCountry(),
            c.isActive()
        )));
    }
}