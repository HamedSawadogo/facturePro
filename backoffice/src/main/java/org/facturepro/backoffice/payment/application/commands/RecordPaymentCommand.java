package org.facturepro.backoffice.payment.application.commands;

import org.facturepro.backoffice.payment.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RecordPaymentCommand(
    UUID tenantId,
    UUID invoiceId,
    BigDecimal amount,
    String currency,
    PaymentMethod method,
    LocalDate paymentDate,
    String reference,
    String idempotencyKey,
    String notes
) {}