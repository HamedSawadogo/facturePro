package org.facturepro.backoffice.payment.infrastructure.adapters;

import org.facturepro.backoffice.payment.domain.entities.Payment;
import org.facturepro.backoffice.payment.domain.repositories.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, UUID>, PaymentRepository {

    Optional<Payment> findByIdempotencyKey(String key);

    Optional<Payment> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Payment> findAllByInvoiceIdAndTenantId(UUID invoiceId, UUID tenantId, Pageable pageable);
}