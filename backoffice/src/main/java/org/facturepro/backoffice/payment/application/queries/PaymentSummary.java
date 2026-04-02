package org.facturepro.backoffice.payment.application.queries;

import org.facturepro.backoffice.payment.domain.enums.PaymentMethod;
import org.facturepro.backoffice.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentSummary(
    UUID id,
    UUID invoiceId,
    BigDecimal amount,
    String currency,
    PaymentMethod method,
    PaymentStatus status,
    LocalDate paymentDate,
    String reference,
    String notes,
    LocalDateTime createdAt
) {}
