package org.facturepro.backoffice.payment.application.commands;

import org.facturepro.backoffice.invoice.domain.entities.Invoice;
import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.facturepro.backoffice.payment.domain.entities.Payment;
import org.facturepro.backoffice.payment.domain.events.PaymentReceivedEvent;
import org.facturepro.backoffice.payment.domain.repositories.PaymentRepository;
import org.facturepro.backoffice.shared.domain.exceptions.IdempotencyException;
import org.facturepro.backoffice.shared.domain.exceptions.ResourceNotFoundException;
import org.facturepro.backoffice.shared.domain.valueObjects.Money;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Use Case : enregistrement d'un paiement.
 * Idempotent — critique pour les callbacks Mobile Money (peuvent arriver plusieurs fois).
 * Met à jour le statut de la facture dans la même transaction.
 */
@Service
@Transactional
public class RecordPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RecordPaymentUseCase(
        final PaymentRepository paymentRepository,
        final InvoiceRepository invoiceRepository,
        final ApplicationEventPublisher eventPublisher
    ) {
        this.paymentRepository = Objects.requireNonNull(paymentRepository);
        this.invoiceRepository = Objects.requireNonNull(invoiceRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public ResourceCreatedId execute(final RecordPaymentCommand command) {
        // Idempotency check — les callbacks Mobile Money peuvent arriver plusieurs fois
        paymentRepository.findByIdempotencyKey(command.idempotencyKey())
            .ifPresent(existing -> {
                throw new IdempotencyException(command.idempotencyKey());
            });

        final Invoice invoice = invoiceRepository.findByIdAndTenantId(command.invoiceId(), command.tenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Facture", command.invoiceId()));

        final Money amount = Money.of(command.amount(), command.currency());

        final Payment payment = Payment.create(
            command.tenantId(),
            command.invoiceId(),
            amount,
            command.method(),
            command.paymentDate(),
            command.reference(),
            command.idempotencyKey(),
            command.notes()
        );

        // Le paiement est confirmé immédiatement (pas de workflow async pour cette v1)
        payment.confirm();

        // Mise à jour de la facture dans la même transaction — cohérence garantie
        invoice.recordPayment(amount);

        final Payment saved = paymentRepository.save(payment);

        eventPublisher.publishEvent(new PaymentReceivedEvent(
            command.tenantId(),
            saved.getId(),
            command.invoiceId(),
            command.amount(),
            command.currency()
        ));

        return ResourceCreatedId.of(saved.getId());
    }
}