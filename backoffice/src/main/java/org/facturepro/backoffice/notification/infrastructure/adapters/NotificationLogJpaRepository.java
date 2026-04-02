package org.facturepro.backoffice.notification.infrastructure.adapters;

import org.facturepro.backoffice.notification.domain.entities.NotificationLog;
import org.facturepro.backoffice.notification.domain.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogJpaRepository extends JpaRepository<NotificationLog, UUID> {

    List<NotificationLog> findAllByInvoiceIdAndType(UUID invoiceId, NotificationType type);

    boolean existsByInvoiceIdAndTypeAndSentAtAfter(UUID invoiceId, NotificationType type, Instant after);
}
