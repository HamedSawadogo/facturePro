package org.facturepro.backoffice.invoice.infrastructure.eventListeners;

import org.facturepro.backoffice.invoice.domain.events.InvoiceCreatedEvent;
import org.facturepro.backoffice.invoice.domain.events.InvoiceStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Réactions aux événements facture.
 * AFTER_COMMIT garantit que la facture est persistée avant toute action async.
 * @Async permet de ne pas bloquer la transaction principale.
 */
@Component
public class InvoiceEventListener {

    private static final Logger log = LoggerFactory.getLogger(InvoiceEventListener.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvoiceCreated(final InvoiceCreatedEvent event) {
        log.info("Facture créée [{}] — déclenchement génération PDF et notification",
            event.getInvoiceNumber());
        // TODO : appeler le service PDF + service notification
        // Ex: pdfService.generateAsync(event.getInvoiceId());
        // Ex: notificationService.sendCreationConfirmation(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvoiceStatusChanged(final InvoiceStatusChangedEvent event) {
        log.info("Statut facture [{}] changé: {} → {}",
            event.getInvoiceNumber(), event.getPreviousStatus(), event.getNewStatus());
        // TODO : notifier le client si SENT ou PAID
    }
}