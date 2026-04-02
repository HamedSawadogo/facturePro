import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { ApiService } from '../../../core/api/api.service';
import {
  METHOD_ICONS, METHOD_LABELS,
  PaymentMethod, PaymentStatus,
  PaymentSummary,
  STATUS_COLORS_PAY, STATUS_LABELS_PAY
} from '../../../core/models/payment.model';
import { InvoiceSummary, STATUS_COLORS, STATUS_LABELS } from '../../../core/models/invoice.model';

@Component({
  selector: 'fp-payment-list',
  standalone: true,
  imports: [RouterLink, FormsModule, DecimalPipe],
  templateUrl: './payment-list.html',
})
export class PaymentListComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly loadingInvoices = signal(true);
  readonly loading = signal(false);
  readonly invoices = signal<InvoiceSummary[]>([]);
  readonly payments = signal<PaymentSummary[]>([]);

  selectedInvoiceId = '';

  readonly confirmedTotal = () =>
    this.payments()
      .filter(p => p.status === 'CONFIRMED')
      .reduce((sum, p) => sum + p.amount, 0);

  readonly selectedInvoice = () =>
    this.invoices().find(i => i.id === this.selectedInvoiceId) ?? null;

  ngOnInit(): void {
    this.api.getInvoices(undefined, undefined, 0, 100).subscribe({
      next: res => {
        this.invoices.set(res.content);
        this.loadingInvoices.set(false);
      },
      error: () => this.loadingInvoices.set(false),
    });
  }

  onInvoiceSelect(invoiceId: string): void {
    this.selectedInvoiceId = invoiceId;
    this.payments.set([]);
    if (!invoiceId) return;
    this.loading.set(true);
    this.api.getPayments(invoiceId).subscribe({
      next: res => {
        this.payments.set(res.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  methodLabel(method: PaymentMethod): string { return METHOD_LABELS[method] ?? method; }
  methodIcon(method: PaymentMethod): string  { return METHOD_ICONS[method] ?? '💳'; }
  statusLabel(status: PaymentStatus): string { return STATUS_LABELS_PAY[status] ?? status; }
  statusColor(status: PaymentStatus): string { return STATUS_COLORS_PAY[status] ?? ''; }

  statusLabelInv(status: string): string { return STATUS_LABELS[status as keyof typeof STATUS_LABELS] ?? status; }
  statusColorInv(status: string): string { return STATUS_COLORS[status as keyof typeof STATUS_COLORS] ?? ''; }
}
