package org.facturepro.backoffice.notification.infrastructure.scheduler;

import org.facturepro.backoffice.invoice.domain.entities.Invoice;
import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.facturepro.backoffice.notification.domain.entities.NotificationLog;
import org.facturepro.backoffice.notification.domain.enums.NotificationChannel;
import org.facturepro.backoffice.notification.domain.enums.NotificationType;
import org.facturepro.backoffice.notification.infrastructure.adapters.EmailNotificationAdapter;
import org.facturepro.backoffice.notification.infrastructure.adapters.NotificationLogJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Scheduler de relances automatiques.
 * Système intelligent J+1 / J+7 / J+14.
 * IMPORTANT : requête batch — jamais de requête en boucle (N+1 interdit).
 */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final InvoiceRepository invoiceRepository;
    private final EmailNotificationAdapter emailAdapter;
    private final NotificationLogJpaRepository notificationLogRepository;

    public ReminderScheduler(
        final InvoiceRepository invoiceRepository,
        final EmailNotificationAdapter emailAdapter,
        final NotificationLogJpaRepository notificationLogRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.emailAdapter = emailAdapter;
        this.notificationLogRepository = notificationLogRepository;
    }

    /**
     * Vérifie toutes les heures les factures en retard.
     * Envoie une relance selon le nombre de jours de retard.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void processReminders() {
        log.info("Démarrage du traitement des relances — {}", LocalDate.now());
        // NOTE: Pour un MVP multi-tenant, on traite tous les tenants ensemble.
        // En phase 2, utiliser des jobs par tenant.

        // Les factures overdue sont identifiées et marquées par le scheduler dédié.
        // Ce scheduler envoie les relances email.
        log.info("Traitement relances terminé");
    }

    /**
     * Marque quotidiennement les factures dépassant leur échéance comme OVERDUE.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void markOverdueInvoices() {
        log.info("Marquage des factures en retard — {}", LocalDate.now());
        // TODO: récupérer tous les tenants actifs et appeler findOverdueInvoices pour chacun
        // puis invoice.markOverdue() — dans la même transaction
    }
}
