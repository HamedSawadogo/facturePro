package org.facturepro.backoffice.payment.web;

import jakarta.validation.Valid;
import org.facturepro.backoffice.payment.application.commands.RecordPaymentCommand;
import org.facturepro.backoffice.payment.application.commands.RecordPaymentUseCase;
import org.facturepro.backoffice.shared.infrastructure.multitenancy.TenantAwarePrincipal;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Paiements.
 * Header X-Idempotency-Key OBLIGATOIRE — critique Mobile Money.
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final RecordPaymentUseCase recordPaymentUseCase;

    public PaymentController(final RecordPaymentUseCase recordPaymentUseCase) {
        this.recordPaymentUseCase = recordPaymentUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResourceCreatedId recordPayment(
        @Valid @RequestBody final RecordPaymentRequest request,
        @RequestHeader("X-Idempotency-Key") final String idempotencyKey,
        @AuthenticationPrincipal final TenantAwarePrincipal principal
    ) {
        return recordPaymentUseCase.execute(new RecordPaymentCommand(
            principal.tenantId(),
            request.invoiceId(),
            request.amount(),
            request.currency(),
            request.method(),
            request.paymentDate(),
            request.reference(),
            idempotencyKey,
            request.notes()
        ));
    }
}
