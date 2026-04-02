package org.facturepro.backoffice.invoice.application.queries;

import org.facturepro.backoffice.invoice.domain.entities.Invoice;
import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.facturepro.backoffice.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;

    public GetInvoiceUseCase(final InvoiceRepository invoiceRepository) {
        this.invoiceRepository = Objects.requireNonNull(invoiceRepository);
    }

    public InvoiceSummary execute(final UUID invoiceId, final UUID tenantId) {
        final Invoice i = invoiceRepository.findByIdAndTenantId(invoiceId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Facture", invoiceId));

        return new InvoiceSummary(
            i.getId(),
            i.getInvoiceNumber().getValue(),
            i.getType(),
            i.getStatus(),
            i.getClientName(),
            i.getIssueDate(),
            i.getDueDate(),
            i.getTotalTtc().getAmount(),
            i.getTotalTtc().getCurrency(),
            i.getAmountPaid().getAmount()
        );
    }
}