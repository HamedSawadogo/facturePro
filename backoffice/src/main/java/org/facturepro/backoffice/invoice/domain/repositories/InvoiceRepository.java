package org.facturepro.backoffice.invoice.domain.repositories;

import org.facturepro.backoffice.invoice.domain.entities.Invoice;
import org.facturepro.backoffice.invoice.domain.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Invoice> findByIdempotencyKey(String key);

    Page<Invoice> findAllByTenantId(UUID tenantId, Pageable pageable);

    Page<Invoice> findAllByTenantIdAndStatus(UUID tenantId, InvoiceStatus status, Pageable pageable);

    Page<Invoice> findAllByTenantIdAndClientId(UUID tenantId, UUID clientId, Pageable pageable);

    /** Factures envoyées dépassant la date d'échéance — pour le scheduler de relances */
    List<Invoice> findOverdueInvoices(UUID tenantId, LocalDate before);

    int countLastInvoiceSequence(UUID tenantId, int year);
}