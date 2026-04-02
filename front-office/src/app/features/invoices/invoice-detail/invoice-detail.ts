import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/api/api.service';
import { InvoiceSummary, STATUS_COLORS, STATUS_LABELS } from '../../../core/models/invoice.model';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'fp-invoice-detail',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  template: `
    <div class="max-w-2xl space-y-5">

      <div class="flex items-center gap-3">
        <a routerLink="/invoices" class="text-slate-400 hover:text-slate-600">← Retour</a>
        <h2 class="text-2xl font-bold text-slate-800">Détail facture</h2>
      </div>

      @if (loading()) {
        <div class="p-10 text-center text-slate-400 text-sm">Chargement...</div>
      } @else if (invoice()) {
        <div class="bg-white rounded-xl border border-slate-200 p-6 space-y-4">

          <div class="flex items-start justify-between">
            <div>
              <p class="font-mono text-lg font-bold text-slate-800">{{ invoice()!.invoiceNumber }}</p>
              <p class="text-slate-500 text-sm">{{ invoice()!.clientName }}</p>
            </div>
            <span [class]="'px-3 py-1 rounded-full text-sm font-medium ' + statusColor(invoice()!.status)">
              {{ statusLabel(invoice()!.status) }}
            </span>
          </div>

          <div class="grid grid-cols-2 gap-3 text-sm border-t border-slate-100 pt-4">
            <div>
              <p class="text-slate-500">Date émission</p>
              <p class="font-medium">{{ invoice()!.issueDate }}</p>
            </div>
            <div>
              <p class="text-slate-500">Échéance</p>
              <p class="font-medium" [class.text-red-600]="invoice()!.status === 'OVERDUE'">
                {{ invoice()!.dueDate }}
              </p>
            </div>
            <div>
              <p class="text-slate-500">Total TTC</p>
              <p class="text-xl font-bold text-slate-800">
                {{ invoice()!.totalTtcAmount | number:'1.0-0' }} {{ invoice()!.currency }}
              </p>
            </div>
            <div>
              <p class="text-slate-500">Montant payé</p>
              <p class="text-xl font-bold text-green-600">
                {{ invoice()!.amountPaidAmount | number:'1.0-0' }} {{ invoice()!.currency }}
              </p>
            </div>
          </div>

          @if (invoice()!.status === 'DRAFT') {
            <div class="border-t border-slate-100 pt-4">
              <button
                (click)="sendInvoice()"
                [disabled]="sending()"
                class="bg-blue-700 hover:bg-blue-800 disabled:bg-blue-300
                       text-white px-4 py-2 rounded-lg text-sm font-medium"
              >
                @if (sending()) { Envoi... } @else { Envoyer la facture }
              </button>
            </div>
          }

        </div>
      }
    </div>
  `,
})
export class InvoiceDetailComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);

  readonly loading = signal(true);
  readonly sending = signal(false);
  readonly invoice = signal<InvoiceSummary | null>(null);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.api.getInvoice(id).subscribe({
      next: inv => {
        this.invoice.set(inv);
        this.loading.set(false);
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
      },
      error: () => this.sending.set(false),
    });
  }

  statusLabel(status: string): string {
    return STATUS_LABELS[status as keyof typeof STATUS_LABELS] ?? status;
  }

  statusColor(status: string): string {
    return STATUS_COLORS[status as keyof typeof STATUS_COLORS] ?? '';
  }
}
