package org.facturepro.backoffice.invoice.application.commands;

import org.facturepro.backoffice.invoice.domain.repositories.InvoiceRepository;
import org.facturepro.backoffice.invoice.domain.valueObjects.InvoiceNumber;
import org.facturepro.backoffice.shared.domain.exceptions.ResourceNotFoundException;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.UUID;

/**
 * Convertit un devis (QUOTE) en facture (INVOICE) draft.
 * Génère un nouveau numéro FAC-XXXX-NNN séquentiel.
 */
@Service
@Transactional
public class ConvertQuoteUseCase {

    private final InvoiceRepository invoiceRepository;

    public ConvertQuoteUseCase(final InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public ResourceCreatedId execute(final UUID quoteId, final UUID tenantId) {
        final var quote = invoiceRepository.findByIdAndTenantId(quoteId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Devis", quoteId));

        final int sequence = invoiceRepository.countLastInvoiceSequence(tenantId, Year.now().getValue()) + 1;
        final InvoiceNumber newNumber = InvoiceNumber.of("FAC", sequence);

        quote.convertToInvoice(newNumber);
        final var saved = invoiceRepository.save(quote);

        return ResourceCreatedId.of(saved.getId());
    }
}
