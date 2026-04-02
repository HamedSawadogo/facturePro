package org.facturepro.backoffice.invoice.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.facturepro.backoffice.invoice.domain.enums.InvoiceType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
    @NotNull InvoiceType type,
    @NotNull UUID clientId,
    @NotBlank String clientName,
    @NotNull LocalDate issueDate,
    @NotNull LocalDate dueDate,
    @NotNull @Size(min = 1) @Valid List<ItemRequest> items,
    String notes,
    String paymentTerms
) {
    public record ItemRequest(
        @NotBlank String description,
        @NotNull @DecimalMin("0.01") BigDecimal quantity,
        @NotNull @DecimalMin("0") BigDecimal unitPriceAmount,
        @NotBlank String currency,
        @DecimalMin("0") @DecimalMax("100") BigDecimal taxRate
    ) {}
}