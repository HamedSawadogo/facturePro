package org.facturepro.backoffice.invoice.application.queries;

import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.facturepro.backoffice.shared.web.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public  class ListInvoicesUseCase {

    private final InvoiceRepository invoiceRepository;

    public ListInvoicesUseCase(final InvoiceRepository invoiceRepository) {
        this.invoiceRepository = Objects.requireNonNull(invoiceRepository);
    }

    public PageResponse<InvoiceSummary> execute(final ListInvoicesQuery query) {
        final PageRequest pageable = PageRequest.of(
            query.page(), query.size(),
            Sort.by(Sort.Direction.DESC, "issueDate")
        );

        final var page = query.status() != null
            ? invoiceRepository.findAllByTenantIdAndStatus(query.tenantId(), query.status(), pageable)
            : query.clientId() != null
                ? invoiceRepository.findAllByTenantIdAndClientId(query.tenantId(), query.clientId(), pageable)
                : invoiceRepository.findAllByTenantId(query.tenantId(), pageable);

        return PageResponse.of(page.map(i -> new InvoiceSummary(
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
        )));
    }
}