package org.facturepro.backoffice.payment.application.queries;

import org.facturepro.backoffice.payment.domain.entities.Payment;
import org.facturepro.backoffice.payment.domain.repositories.PaymentRepository;
import org.facturepro.backoffice.shared.web.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListPaymentsUseCase {

    private final PaymentRepository paymentRepository;

    public ListPaymentsUseCase(final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PageResponse<PaymentSummary> execute(
        final UUID invoiceId,
        final UUID tenantId,
        final int page,
        final int size
    ) {
        final Page<Payment> paged = paymentRepository.findAllByInvoiceIdAndTenantId(
            invoiceId, tenantId,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return PageResponse.of(paged.map(this::toSummary));
    }

    private PaymentSummary toSummary(final Payment p) {
        return new PaymentSummary(
            p.getId(),
            p.getInvoiceId(),
            p.getAmount().getAmount(),
            p.getAmount().getCurrency(),
            p.getMethod(),
            p.getStatus(),
            p.getPaymentDate(),
            p.getReference(),
            p.getNotes(),
            p.getCreatedAt() != null ? p.getCreatedAt().atZone(java.time.ZoneOffset.UTC).toLocalDateTime() : null
        );
    }
}
