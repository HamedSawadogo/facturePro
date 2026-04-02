package org.facturepro.backoffice.payment.domain.repositories;

import org.facturepro.backoffice.payment.domain.entities.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByIdempotencyKey(String key);

    Optional<Payment> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Payment> findAllByInvoiceIdAndTenantId(UUID invoiceId, UUID tenantId, Pageable pageable);
}