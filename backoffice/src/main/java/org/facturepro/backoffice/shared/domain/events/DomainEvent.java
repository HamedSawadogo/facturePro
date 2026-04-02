package org.facturepro.backoffice.shared.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Contrat de base pour tous les événements domaine.
 * Publié via ApplicationEventPublisher — consommé par @TransactionalEventListener(AFTER_COMMIT).
 */
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final UUID tenantId;

    protected DomainEvent(final UUID tenantId) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.tenantId = tenantId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public UUID getTenantId() {
        return tenantId;
    }
}