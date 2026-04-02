package org.facturepro.backoffice.notification.infrastructure.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Gère les connexions SSE actives par tenant.
 * Thread-safe : ConcurrentHashMap + CopyOnWriteArrayList.
 */
@Service
public class SseNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SseNotificationService.class);

    /** Timeout SSE : 3 minutes — le client doit se reconnecter automatiquement. */
    private static final long SSE_TIMEOUT_MS = 3 * 60 * 1000L;

    private final Map<UUID, List<SseEmitter>> emittersByTenant = new ConcurrentHashMap<>();

    /**
     * Crée et enregistre un emitter SSE pour un tenant donné.
     * Le client Angular se reconnecte automatiquement si la connexion est perdue.
     */
    public SseEmitter subscribe(final UUID tenantId) {
        final SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        final List<SseEmitter> list = emittersByTenant
            .computeIfAbsent(tenantId, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        emitter.onCompletion(() -> remove(tenantId, emitter));
        emitter.onTimeout(() -> remove(tenantId, emitter));
        emitter.onError(ex -> remove(tenantId, emitter));

        // Ping initial pour confirmer la connexion
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            remove(tenantId, emitter);
        }

        log.debug("SSE: nouveau subscriber pour tenant [{}] — {} connexion(s) actives",
            tenantId, list.size());
        return emitter;
    }

    /**
     * Pousse un événement à tous les emitters d'un tenant.
     */
    public void sendToTenant(final UUID tenantId, final String eventName, final Object data) {
        final List<SseEmitter> emitters = emittersByTenant.getOrDefault(tenantId, List.of());
        if (emitters.isEmpty()) return;

        for (final SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                remove(tenantId, emitter);
            }
        }
    }

    private void remove(final UUID tenantId, final SseEmitter emitter) {
        final List<SseEmitter> list = emittersByTenant.get(tenantId);
        if (list != null) {
            list.remove(emitter);
        }
    }
}
