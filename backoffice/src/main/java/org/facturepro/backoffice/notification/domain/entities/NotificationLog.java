package org.facturepro.backoffice.notification.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.facturepro.backoffice.notification.domain.enums.NotificationChannel;
import org.facturepro.backoffice.notification.domain.enums.NotificationType;
import org.facturepro.backoffice.shared.infrastructure.audit.AuditEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit trail des notifications envoyées.
 * Permet de tracer qui a reçu quoi et quand — critique pour les relances.
 */
@Getter
@Entity
@Table(
    name = "notification_logs",
    indexes = {
        @Index(name = "idx_notif_tenant_invoice", columnList = "tenant_id, invoice_id"),
        @Index(name = "idx_notif_type_sent_at", columnList = "type, sent_at")
    }
)
public class NotificationLog extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "subject")
    private String subject;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent_at")
    private Instant sentAt;

    protected NotificationLog() {}

    public static NotificationLog create(
        final UUID tenantId,
        final UUID invoiceId,
        final String recipient,
        final NotificationChannel channel,
        final NotificationType type,
        final String subject
    ) {
        final NotificationLog log = new NotificationLog();
        log.setTenantId(tenantId);
        log.invoiceId = invoiceId;
        log.recipient = recipient;
        log.channel = channel;
        log.type = type;
        log.subject = subject;
        log.sentAt = Instant.now();
        return log;
    }

    public void markSuccess() {
        this.success = true;
    }

    public void markFailed(final String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
    }
}
