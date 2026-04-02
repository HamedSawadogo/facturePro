package org.facturepro.backoffice.payment.web;

import jakarta.validation.constraints.*;
import org.facturepro.backoffice.payment.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RecordPaymentRequest(
    @NotNull UUID invoiceId,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    @NotBlank String currency,
    @NotNull PaymentMethod method,
    @NotNull LocalDate paymentDate,
    String reference,
    String notes
) {}
