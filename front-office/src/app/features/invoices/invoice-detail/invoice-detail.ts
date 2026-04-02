import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/api/api.service';
import { InvoiceSummary, InvoiceType, STATUS_COLORS, STATUS_LABELS } from '../../../core/models/invoice.model';
import { Client } from '../../../core/models/client.model';
import { DecimalPipe } from '@angular/common';

interface InvoiceItem {
  description: string;
  quantity: number;
  unitPrice: number;
  taxRate: number;
}

const TYPE_LABELS: Record<InvoiceType, string> = {
  INVOICE: 'FACTURE',
  QUOTE: 'DEVIS',
  CREDIT_NOTE: 'AVOIR',
};

@Component({
  selector: 'fp-invoice-detail',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  templateUrl: './invoice-detail.html',
  styles: [`
    @media print {
      :host { display: block; }
      .invoice-document { max-width: 100%; }
    }
  `],
})
export class InvoiceDetailComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly loading = signal(true);
  readonly sending = signal(false);
  readonly converting = signal(false);
  readonly copied = signal(false);
  readonly sendSuccess = signal(false);
  readonly invoice = signal<InvoiceSummary | null>(null);
  readonly client = signal<Client | null>(null);

  readonly items = signal<InvoiceItem[]>([]);

  readonly whatsappUrl = computed(() => {
    const inv = this.invoice();
    const cl = this.client();
    if (!inv) return null;
    const phone = cl?.phone?.replace(/\D/g, '') ?? '';
    const msg = encodeURIComponent(
      `Bonjour ${inv.clientName},\n\nVeuillez trouver ci-joint votre ${TYPE_LABELS[inv.type]} n° ${inv.invoiceNumber} d'un montant de ${inv.totalTtcAmount.toLocaleString()} ${inv.currency}.\n\nMerci pour votre confiance.`
    );
    const base = phone ? `https://wa.me/${phone}` : `https://wa.me/`;
    return `${base}?text=${msg}`;
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.api.getInvoice(id).subscribe({
      next: inv => {
        this.invoice.set(inv);
        this.loading.set(false);
        if (inv.clientId) {
          this.api.getClient(inv.clientId).subscribe({
            next: cl => this.client.set(cl),
            error: () => {},
          });
        }
      },
      error: () => this.loading.set(false),
    });
  }

  sendInvoice(): void {
    const id = this.invoice()?.id;
    if (!id) return;
    this.sending.set(true);
    this.api.sendInvoice(id).subscribe({
      next: () => {
        this.invoice.update(i => i ? { ...i, status: 'SENT' } : i);
        this.sending.set(false);
        this.sendSuccess.set(true);
        setTimeout(() => this.sendSuccess.set(false), 3000);
      },
      error: () => this.sending.set(false),
    });
  }

  convertToInvoice(): void {
    const id = this.invoice()?.id;
    if (!id) return;
    this.converting.set(true);
    this.api.convertInvoice(id).subscribe({
      next: res => {
        this.converting.set(false);
        this.router.navigate(['/invoices', res.id]);
      },
      error: () => this.converting.set(false),
    });
  }

  copyLink(): void {
    const id = this.invoice()?.id;
    if (!id) return;
    const url = `${window.location.origin}/invoices/${id}`;
    navigator.clipboard.writeText(url).then(() => {
      this.copied.set(true);
      setTimeout(() => this.copied.set(false), 2000);
    });
  }

  print(): void {
    window.print();
  }

  totalHt(): number {
    return this.invoice()?.totalHtAmount ?? 0;
  }

  tva(): number {
    return (this.invoice()?.totalTtcAmount ?? 0) - this.totalHt();
  }

  statusLabel(status: string): string {
    return STATUS_LABELS[status as keyof typeof STATUS_LABELS] ?? status;
  }

  statusColor(status: string): string {
    return STATUS_COLORS[status as keyof typeof STATUS_COLORS] ?? '';
  }

  typeLabel(type: InvoiceType): string {
    return TYPE_LABELS[type] ?? type;
  }
}
