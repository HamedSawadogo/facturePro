package org.facturepro.backoffice.notification.infrastructure.eventListeners;

import org.facturepro.backoffice.invoice.domain.events.InvoiceCreatedEvent;
import org.facturepro.backoffice.invoice.domain.events.InvoiceStatusChangedEvent;
import org.facturepro.backoffice.notification.domain.entities.NotificationLog;
import org.facturepro.backoffice.notification.domain.enums.NotificationChannel;
import org.facturepro.backoffice.notification.domain.enums.NotificationType;
import org.facturepro.backoffice.notification.infrastructure.adapters.EmailNotificationAdapter;
import org.facturepro.backoffice.notification.infrastructure.adapters.NotificationLogJpaRepository;
import org.facturepro.backoffice.invoice.domain.enums.InvoiceStatus;
import org.facturepro.backoffice.payment.domain.events.PaymentReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener cross-module : réagit aux événements facture et paiement.
 * Toujours AFTER_COMMIT — ne jamais envoyer un email si la transaction a rollbacké.
 */
@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final EmailNotificationAdapter emailAdapter;
    private final NotificationLogJpaRepository notificationLogRepository;

    public NotificationEventListener(
        final EmailNotificationAdapter emailAdapter,
        final NotificationLogJpaRepository notificationLogRepository
    ) {
        this.emailAdapter = emailAdapter;
        this.notificationLogRepository = notificationLogRepository;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvoiceCreated(final InvoiceCreatedEvent event) {
        log.info("Notification: facture créée [{}]", event.getInvoiceNumber());
        // TODO: récupérer l'email du client et envoyer confirmation
        // Exemple:
        // final String html = templateEngine.process("invoice-created", context);
        // emailAdapter.send(clientEmail, "Facture créée - " + event.getInvoiceNumber(), html);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvoiceSent(final InvoiceStatusChangedEvent event) {
        if (event.getNewStatus() != InvoiceStatus.SENT) return;

        log.info("Notification: facture envoyée [{}]", event.getInvoiceNumber());

        final NotificationLog logEntry = NotificationLog.create(
            event.getTenantId(),
            event.getInvoiceId(),
            "client@example.com", // TODO: récupérer l'email du client via query
            NotificationChannel.EMAIL,
            NotificationType.INVOICE_SENT,
            "Facture " + event.getInvoiceNumber()
        );

        try {
            // emailAdapter.send(...)
            logEntry.markSuccess();
        } catch (Exception e) {
            logEntry.markFailed(e.getMessage());
        } finally {
            notificationLogRepository.save(logEntry);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentReceived(final PaymentReceivedEvent event) {
        log.info("Notification: paiement reçu pour facture [{}] — montant: {} {}",
            event.getInvoiceId(), event.getAmount(), event.getCurrency());
        // TODO: envoyer reçu de paiement au client
    }
}
