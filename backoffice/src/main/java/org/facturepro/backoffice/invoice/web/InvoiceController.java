package org.facturepro.backoffice.invoice.web;

import jakarta.validation.Valid;
import org.facturepro.backoffice.invoice.application.commands.ConvertQuoteUseCase;
import org.facturepro.backoffice.invoice.application.commands.CreateInvoiceCommand;
import org.facturepro.backoffice.invoice.application.commands.CreateInvoiceUseCase;
import org.facturepro.backoffice.invoice.application.commands.SendInvoiceUseCase;
import org.facturepro.backoffice.invoice.application.queries.*;
import org.facturepro.backoffice.invoice.domain.enums.InvoiceStatus;
import org.facturepro.backoffice.shared.infrastructure.multitenancy.TenantAwarePrincipal;
import org.facturepro.backoffice.shared.web.PageResponse;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller Factures — core métier FacturePro.
 * Header X-Idempotency-Key obligatoire pour la création.
 */
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final SendInvoiceUseCase sendInvoiceUseCase;
    private final ConvertQuoteUseCase convertQuoteUseCase;
    private final GetInvoiceUseCase getInvoiceUseCase;
    private final ListInvoicesUseCase listInvoicesUseCase;

    public InvoiceController(
        final CreateInvoiceUseCase createInvoiceUseCase,
        final SendInvoiceUseCase sendInvoiceUseCase,
        final ConvertQuoteUseCase convertQuoteUseCase,
        final GetInvoiceUseCase getInvoiceUseCase,
        final ListInvoicesUseCase listInvoicesUseCase
    ) {
        this.createInvoiceUseCase = createInvoiceUseCase;
        this.sendInvoiceUseCase = sendInvoiceUseCase;
        this.convertQuoteUseCase = convertQuoteUseCase;
        this.getInvoiceUseCase = getInvoiceUseCase;
        this.listInvoicesUseCase = listInvoicesUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResourceCreatedId create(
        @Valid @RequestBody final CreateInvoiceRequest request,
        @RequestHeader(value = "X-Idempotency-Key", required = false) final String idempotencyKey,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return createInvoiceUseCase.execute(new CreateInvoiceCommand(
            principal.tenantId(),
            request.type(),
            request.clientId(),
            request.clientName(),
            request.issueDate(),
            request.dueDate(),
            request.items().stream()
                .map(i -> new CreateInvoiceCommand.ItemCommand(
                    i.description(), i.quantity(),
                    i.unitPriceAmount(), i.currency(), i.taxRate()
                )).toList(),
            request.notes(),
            request.paymentTerms(),
            idempotencyKey
        ));
    }

    @PostMapping("/{id}/convert")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResourceCreatedId convertToInvoice(
        @PathVariable final UUID id,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return convertQuoteUseCase.execute(id, principal.tenantId());
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResourceCreatedId send(
        @PathVariable final UUID id,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return sendInvoiceUseCase.execute(id, principal.tenantId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'VIEWER')")
    public InvoiceSummary getOne(
        @PathVariable final UUID id,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return getInvoiceUseCase.execute(id, principal.tenantId());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'VIEWER')")
    public PageResponse<InvoiceSummary> list(
        @RequestParam(required = false) final InvoiceStatus status,
        @RequestParam(required = false) final UUID clientId,
        @RequestParam(defaultValue = "0") final int page,
        @RequestParam(defaultValue = "20") final int size,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return listInvoicesUseCase.execute(new ListInvoicesQuery(
            principal.tenantId(), status, clientId, page, size
        ));
    }
}