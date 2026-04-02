package org.facturepro.backoffice.notification.infrastructure.sse;

import org.facturepro.backoffice.shared.infrastructure.multitenancy.TenantAwarePrincipal;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Endpoint SSE — le front Angular s'abonne ici pour recevoir les notifications temps réel.
 * Le JWT est passé en query param (?token=...) car l'API EventSource du navigateur
 * ne supporte pas les headers personnalisés.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class SseController {

    private final SseNotificationService sseService;

    public SseController(final SseNotificationService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return sseService.subscribe(principal.tenantId());
    }
}
