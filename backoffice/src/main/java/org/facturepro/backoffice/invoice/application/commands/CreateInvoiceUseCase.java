package org.facturepro.backoffice.invoice.application.commands;

import org.facturepro.backoffice.invoice.domain.entities.Invoice;
import org.facturepro.backoffice.invoice.domain.entities.InvoiceItem;
import org.facturepro.backoffice.invoice.domain.events.InvoiceCreatedEvent;
import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.facturepro.backoffice.invoice.domain.valueObjects.InvoiceNumber;
import org.facturepro.backoffice.shared.domain.exceptions.IdempotencyException;
import org.facturepro.backoffice.shared.domain.valueObjects.Money;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Objects;

/**
 * Use Case : création d'une facture ou d'un devis.
 * Idempotent — rejette les doublons via idempotency key.
 */
@Service
@Transactional
public  class CreateInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateInvoiceUseCase(
        final InvoiceRepository invoiceRepository,
        final ApplicationEventPublisher eventPublisher
    ) {
        this.invoiceRepository = Objects.requireNonNull(invoiceRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public ResourceCreatedId execute(final CreateInvoiceCommand command) {
        // Idempotency check — critique pour éviter les doubles factures
        if (command.idempotencyKey() != null) {
            invoiceRepository.findByIdempotencyKey(command.idempotencyKey())
                .ifPresent(existing -> {
                    throw new IdempotencyException(command.idempotencyKey());
                });
        }

        // Génération du numéro séquentiel par tenant
        final String prefix = switch (command.type()) {
            case INVOICE -> "FAC";
            case QUOTE -> "DEV";
            case CREDIT_NOTE -> "AVO";
        };
        final int sequence = invoiceRepository.countLastInvoiceSequence(
            command.tenantId(), Year.now().getValue()
        ) + 1;
        final InvoiceNumber invoiceNumber = InvoiceNumber.of(prefix, sequence);

        // Création de l'agrégat
        final Invoice invoice = Invoice.create(
            command.tenantId(),
            invoiceNumber,
            command.type(),
            command.clientId(),
            command.clientName(),
            command.issueDate(),
            command.dueDate(),
            command.notes(),
            command.paymentTerms(),
            command.idempotencyKey()
        );

        // Ajout des lignes via les méthodes de l'agrégat (pas de setter externe)
        command.items().forEach(item -> {
            final InvoiceItem invoiceItem = InvoiceItem.create(
                invoice,
                item.description(),
                item.quantity(),
                Money.of(item.unitPriceAmount(), item.currency()),
                item.taxRate()
            );
            invoice.addItem(invoiceItem);
        });

        final Invoice saved = invoiceRepository.save(invoice);

        // Publication APRÈS commit — garantit la cohérence avec @TransactionalEventListener
        eventPublisher.publishEvent(new InvoiceCreatedEvent(
            saved.getTenantId(),
            saved.getId(),
            saved.getInvoiceNumber().getValue(),
            saved.getClientId(),
            saved.getClientName(),
            saved.getType()
        ));

        return ResourceCreatedId.of(saved.getId());
    }
}