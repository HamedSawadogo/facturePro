package org.facturepro.backoffice.invoice.infrastructure.adapters;

import org.facturepro.backoffice.invoice.domain.entities.Invoice;
import org.facturepro.backoffice.invoice.domain.enums.InvoiceStatus;
import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceJpaRepository extends JpaRepository<Invoice, UUID>, InvoiceRepository {

    // @EntityGraph pour éviter le N+1 sur les items lors des détails
    @EntityGraph(attributePaths = {"items"})
    Optional<Invoice> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Invoice> findByIdempotencyKey(String key);

    Page<Invoice> findAllByTenantId(UUID tenantId, Pageable pageable);

    Page<Invoice> findAllByTenantIdAndStatus(UUID tenantId, InvoiceStatus status, Pageable pageable);

    Page<Invoice> findAllByTenantIdAndClientId(UUID tenantId, UUID clientId, Pageable pageable);

    @Query("""
        SELECT i FROM Invoice i
        WHERE i.tenantId = :tenantId
        AND i.status IN ('SENT', 'PARTIALLY_PAID')
        AND i.dueDate < :before
        """)
    List<Invoice> findOverdueInvoices(
        @Param("tenantId") UUID tenantId,
        @Param("before") LocalDate before
    );

    @Query("""
        SELECT COALESCE(MAX(
            CAST(SUBSTRING(i.invoiceNumber.value, LENGTH(i.invoiceNumber.value) - 5) AS int)
        ), 0)
        FROM Invoice i
        WHERE i.tenantId = :tenantId
        AND SUBSTRING(i.invoiceNumber.value, 5, 4) = CAST(:year AS string)
        """)
    int countLastInvoiceSequence(
        @Param("tenantId") UUID tenantId,
        @Param("year") int year
    );
}