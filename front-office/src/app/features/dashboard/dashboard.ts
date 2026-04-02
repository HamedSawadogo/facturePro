import { Component, inject, OnInit, signal } from '@angular/core';
import { ApiService } from '../../core/api/api.service';
import { InvoiceSummary, STATUS_COLORS, STATUS_LABELS } from '../../core/models/invoice.model';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'fp-dashboard',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  templateUrl: './dashboard.html',
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly loading = signal(true);
  readonly recentInvoices = signal<InvoiceSummary[]>([]);

  readonly sentCount = () => this.recentInvoices().filter(i => i.status === 'SENT').length;
  readonly overdueCount = () => this.recentInvoices().filter(i => i.status === 'OVERDUE').length;
  readonly paidCount = () => this.recentInvoices().filter(i => i.status === 'PAID').length;

  readonly totalAmount = () =>
    this.recentInvoices().reduce((sum, i) => sum + i.totalTtcAmount, 0);
  readonly sentAmount = () =>
    this.recentInvoices().filter(i => i.status === 'SENT').reduce((sum, i) => sum + i.totalTtcAmount, 0);
  readonly paidAmount = () =>
    this.recentInvoices().filter(i => i.status === 'PAID').reduce((sum, i) => sum + i.totalTtcAmount, 0);

  ngOnInit(): void {
    this.api.getInvoices(undefined, undefined, 0, 10).subscribe({
      next: res => {
        this.recentInvoices.set(res.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  statusLabel(status: string): string {
    return STATUS_LABELS[status as keyof typeof STATUS_LABELS] ?? status;
  }

  statusColor(status: string): string {
    return STATUS_COLORS[status as keyof typeof STATUS_COLORS] ?? '';
  }
}
