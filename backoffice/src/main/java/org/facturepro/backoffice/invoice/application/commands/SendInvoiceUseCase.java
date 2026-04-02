package org.facturepro.backoffice.invoice.application.commands;

import org.facturepro.backoffice.invoice.domain.entities.Invoice;
import org.facturepro.backoffice.invoice.domain.events.InvoiceStatusChangedEvent;
import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.facturepro.backoffice.shared.domain.exceptions.ResourceNotFoundException;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Use Case : envoi d'une facture (DRAFT → SENT).
 * Déclenche l'envoi email via événement.
 */
@Service
@Transactional
public  class SendInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SendInvoiceUseCase(
        final InvoiceRepository invoiceRepository,
        final ApplicationEventPublisher eventPublisher
    ) {
        this.invoiceRepository = Objects.requireNonNull(invoiceRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public ResourceCreatedId execute(final UUID invoiceId, final UUID tenantId) {
        final Invoice invoice = invoiceRepository.findByIdAndTenantId(invoiceId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Facture", invoiceId));

        final var previousStatus = invoice.getStatus();
        invoice.send();

        eventPublisher.publishEvent(new InvoiceStatusChangedEvent(
            tenantId, invoiceId,
            invoice.getInvoiceNumber().getValue(),
            previousStatus,
            invoice.getStatus(),
            invoice.getClientId()
        ));

        return ResourceCreatedId.of(invoice.getId());
    }
}