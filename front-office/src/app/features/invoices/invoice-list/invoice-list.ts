import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/api/api.service';
import { InvoiceSummary, InvoiceStatus, STATUS_COLORS, STATUS_LABELS } from '../../../core/models/invoice.model';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'fp-invoice-list',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  template: `
    <div class="space-y-5">

      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-slate-800">Factures</h2>
          <p class="text-slate-500 text-sm mt-0.5">{{ total() }} document(s)</p>
        </div>
        <a routerLink="new"
           class="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded-lg text-sm font-medium">
          + Nouvelle facture
        </a>
      </div>

      <!-- Status filter tabs -->
      <div class="flex gap-2 overflow-x-auto pb-1">
        @for (tab of tabs; track tab.value) {
          <button
            (click)="filterStatus.set(tab.value)"
            [class.bg-blue-700]="filterStatus() === tab.value"
            [class.text-white]="filterStatus() === tab.value"
            [class.bg-white]="filterStatus() !== tab.value"
            [class.text-slate-600]="filterStatus() !== tab.value"
            class="px-4 py-1.5 rounded-full text-sm font-medium border border-slate-200 whitespace-nowrap"
          >
            {{ tab.label }}
          </button>
        }
      </div>

      <!-- Table -->
      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        @if (loading()) {
          <div class="p-10 text-center text-slate-400 text-sm">Chargement...</div>
        } @else if (invoices().length === 0) {
          <div class="p-10 text-center">
            <p class="text-slate-400 text-sm">Aucune facture trouvée.</p>
            <a routerLink="new" class="text-blue-600 hover:underline text-sm mt-2 inline-block">
              Créer une facture →
            </a>
          </div>
        } @else {
          <table class="w-full text-sm">
            <thead class="bg-slate-50 border-b border-slate-200">
              <tr>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase">N°</th>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase">Client</th>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase">Date émission</th>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase">Échéance</th>
                <th class="text-right px-6 py-3 text-xs font-semibold text-slate-500 uppercase">Montant TTC</th>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase">Statut</th>
                <th class="px-6 py-3"></th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              @for (inv of invoices(); track inv.id) {
                <tr class="hover:bg-slate-50">
                  <td class="px-6 py-3 font-mono text-xs font-semibold text-slate-700">
                    {{ inv.invoiceNumber }}
                  </td>
                  <td class="px-6 py-3 text-slate-800 font-medium">{{ inv.clientName }}</td>
                  <td class="px-6 py-3 text-slate-600">{{ inv.issueDate }}</td>
                  <td class="px-6 py-3" [class.text-red-600]="inv.status === 'OVERDUE'">
                    {{ inv.dueDate }}
                  </td>
                  <td class="px-6 py-3 text-right font-semibold text-slate-800">
                    {{ inv.totalTtcAmount | number:'1.0-0' }} {{ inv.currency }}
                  </td>
                  <td class="px-6 py-3">
                    <span [class]="'px-2.5 py-0.5 rounded-full text-xs font-medium ' + statusColor(inv.status)">
                      {{ statusLabel(inv.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-3 text-right">
                    <a [routerLink]="inv.id" class="text-blue-600 hover:underline text-xs">Voir</a>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        }
      </div>

    </div>
  `,
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
