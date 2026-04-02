import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/api/api.service';
import { InvoiceSummary, InvoiceStatus, STATUS_COLORS, STATUS_LABELS } from '../../../core/models/invoice.model';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'fp-invoice-list',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  templateUrl: './invoice-list.html',
})
export class InvoiceListComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly loading = signal(true);
  readonly invoices = signal<InvoiceSummary[]>([]);
  readonly total = signal(0);
  readonly filterStatus = signal<string | undefined>(undefined);

  readonly tabs = [
    { label: 'Toutes', value: undefined },
    { label: 'Brouillons', value: 'DRAFT' },
    { label: 'Envoyées', value: 'SENT' },
    { label: 'En retard', value: 'OVERDUE' },
    { label: 'Payées', value: 'PAID' },
  ];

  ngOnInit(): void {
    this.load();
  }

  setFilter(value: string | undefined): void {
    this.filterStatus.set(value);
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.api.getInvoices(this.filterStatus()).subscribe({
      next: res => {
        this.invoices.set(res.content);
        this.total.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  statusLabel(status: string): string {
    return STATUS_LABELS[status as InvoiceStatus] ?? status;
  }

  statusColor(status: string): string {
    return STATUS_COLORS[status as InvoiceStatus] ?? '';
  }
}
