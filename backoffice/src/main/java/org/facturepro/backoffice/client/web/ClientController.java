package org.facturepro.backoffice.client.web;

import jakarta.validation.Valid;
import org.facturepro.backoffice.client.application.commands.CreateClientCommand;
import org.facturepro.backoffice.client.application.commands.CreateClientUseCase;
import org.facturepro.backoffice.client.application.commands.UpdateClientCommand;
import org.facturepro.backoffice.client.application.commands.UpdateClientUseCase;
import org.facturepro.backoffice.client.application.queries.*;
import org.facturepro.backoffice.shared.infrastructure.multitenancy.TenantAwarePrincipal;
import org.facturepro.backoffice.shared.web.PageResponse;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller Clients — CRUD + recherche.
 */
@RestController
@RequestMapping("/api/v1/clients")
public  class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final GetClientUseCase getClientUseCase;
    private final ListClientsUseCase listClientsUseCase;

    public ClientController(
        final CreateClientUseCase createClientUseCase,
        final UpdateClientUseCase updateClientUseCase,
        final GetClientUseCase getClientUseCase,
        final ListClientsUseCase listClientsUseCase
    ) {
        this.createClientUseCase = createClientUseCase;
        this.updateClientUseCase = updateClientUseCase;
        this.getClientUseCase = getClientUseCase;
        this.listClientsUseCase = listClientsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResourceCreatedId create(
        @Valid @RequestBody final CreateClientRequest request,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return createClientUseCase.execute(new CreateClientCommand(
            principal.tenantId(),
            request.name(),
            request.email(),
            request.phone(),
            request.clientType(),
            request.address(),
            request.city(),
            request.country(),
            request.taxNumber()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResourceCreatedId update(
        @PathVariable final UUID id,
        @Valid @RequestBody final UpdateClientRequest request,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return updateClientUseCase.execute(new UpdateClientCommand(
            id, principal.tenantId(),
            request.name(), request.email(), request.phone(),
            request.address(), request.city(), request.country(), request.taxNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'VIEWER')")
    public ClientSummary getOne(
        @PathVariable final UUID id,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return getClientUseCase.execute(id, principal.tenantId());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'VIEWER')")
    public PageResponse<ClientSummary> list(
        @RequestParam(defaultValue = "") final String search,
        @RequestParam(defaultValue = "0") final int page,
        @RequestParam(defaultValue = "20") final int size,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return listClientsUseCase.execute(new ListClientsQuery(
            principal.tenantId(), search, page, size
        ));
    }
}