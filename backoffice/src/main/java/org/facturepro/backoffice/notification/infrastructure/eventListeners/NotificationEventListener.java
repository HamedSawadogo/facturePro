package org.facturepro.backoffice.notification.infrastructure.eventListeners;

import org.facturepro.backoffice.client.domain.repositories.ClientRepository;
import org.facturepro.backoffice.invoice.domain.events.InvoiceCreatedEvent;
import org.facturepro.backoffice.invoice.domain.events.InvoiceStatusChangedEvent;
import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.facturepro.backoffice.notification.domain.entities.NotificationLog;
import org.facturepro.backoffice.notification.domain.enums.NotificationChannel;
import org.facturepro.backoffice.notification.domain.enums.NotificationType;
import org.facturepro.backoffice.notification.infrastructure.adapters.EmailNotificationAdapter;
import org.facturepro.backoffice.notification.infrastructure.adapters.NotificationLogJpaRepository;
import org.facturepro.backoffice.notification.infrastructure.sse.SseNotificationService;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

/**
 * Listener cross-module — réagit aux événements facture et paiement.
 * AFTER_COMMIT : jamais d'email si la transaction a rollbacké.
 */
@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);
    private static final String BASE_URL = "http://localhost:4200";

    private final EmailNotificationAdapter emailAdapter;
    private final NotificationLogJpaRepository notificationLogRepository;
    private final SseNotificationService sseService;
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final TemplateEngine templateEngine;

    public NotificationEventListener(
        final EmailNotificationAdapter emailAdapter,
        final NotificationLogJpaRepository notificationLogRepository,
        final SseNotificationService sseService,
        final InvoiceRepository invoiceRepository,
        final ClientRepository clientRepository,
        final TemplateEngine templateEngine
    ) {
        this.emailAdapter = emailAdapter;
        this.notificationLogRepository = notificationLogRepository;
        this.sseService = sseService;
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.templateEngine = templateEngine;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvoiceCreated(final InvoiceCreatedEvent event) {
        log.info("Notification: facture créée [{}]", event.getInvoiceNumber());
        sseService.sendToTenant(event.getTenantId(), "invoice.created", Map.of(
            "invoiceNumber", event.getInvoiceNumber(),
            "message", "Facture " + event.getInvoiceNumber() + " créée"
        ));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvoiceSent(final InvoiceStatusChangedEvent event) {
        if (event.getNewStatus() != InvoiceStatus.SENT) return;
        log.info("Notification: facture envoyée [{}]", event.getInvoiceNumber());

        final var invoice = invoiceRepository
            .findByIdAndTenantId(event.getInvoiceId(), event.getTenantId())
            .orElse(null);

        String clientEmail = null;
        String clientName = event.getInvoiceNumber();

        if (invoice != null) {
            clientName = invoice.getClientName();
            final var client = clientRepository
                .findByIdAndTenantId(invoice.getClientId(), event.getTenantId())
                .orElse(null);
            if (client != null) clientEmail = client.getEmail() != null ? client.getEmail().getValue() : null;
        }

        final NotificationLog logEntry = NotificationLog.create(
            event.getTenantId(),
            event.getInvoiceId(),
            clientEmail != null ? clientEmail : "no-email",
            NotificationChannel.EMAIL,
            NotificationType.INVOICE_SENT,
            "Facture " + event.getInvoiceNumber()
        );

        try {
            if (clientEmail != null && invoice != null) {
                final Context ctx = new Context(Locale.FRENCH);
                ctx.setVariable("invoiceNumber", event.getInvoiceNumber());
                ctx.setVariable("clientName", clientName);
                ctx.setVariable("companyName", "FacturePro Africa");
                ctx.setVariable("issueDate", invoice.getIssueDate().toString());
                ctx.setVariable("dueDate", invoice.getDueDate().toString());
                ctx.setVariable("totalTtc", invoice.getTotalTtc().getAmount().toPlainString());
                ctx.setVariable("currency", invoice.getTotalTtc().getCurrency());
                ctx.setVariable("notes", invoice.getNotes());
                ctx.setVariable("invoiceUrl", BASE_URL + "/invoices/" + invoice.getId());
                ctx.setVariable("whatsappUrl", "https://wa.me/?text=Votre%20facture%20" + event.getInvoiceNumber());

                final String html = templateEngine.process("invoice-sent", ctx);
                emailAdapter.send(clientEmail, "Facture " + event.getInvoiceNumber(), html);
            }
            logEntry.markSuccess();
        } catch (Exception e) {
            logEntry.markFailed(e.getMessage());
            log.error("Échec envoi email facture {}: {}", event.getInvoiceNumber(), e.getMessage());
        } finally {
            notificationLogRepository.save(logEntry);
        }

        sseService.sendToTenant(event.getTenantId(), "invoice.sent", Map.of(
            "invoiceNumber", event.getInvoiceNumber(),
            "message", "Facture " + event.getInvoiceNumber() + " envoyée"
        ));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentReceived(final PaymentReceivedEvent event) {
        log.info("Notification: paiement reçu pour facture [{}] — montant: {} {}",
            event.getInvoiceId(), event.getAmount(), event.getCurrency());
        sseService.sendToTenant(event.getTenantId(), "payment.received", Map.of(
            "invoiceId", event.getInvoiceId().toString(),
            "amount", event.getAmount(),
            "currency", event.getCurrency(),
            "message", "Paiement de " + event.getAmount() + " " + event.getCurrency() + " reçu"
        ));
    }
}
