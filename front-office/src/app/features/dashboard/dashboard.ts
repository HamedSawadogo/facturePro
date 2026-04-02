import { Component, inject, OnInit, signal } from '@angular/core';
import { ApiService } from '../../core/api/api.service';
import { InvoiceSummary, STATUS_COLORS, STATUS_LABELS } from '../../core/models/invoice.model';
import { RouterLink } from '@angular/router';
import {DecimalPipe} from '@angular/common';

interface DashboardStats {
  totalInvoices: number;
  pendingPayments: number;
  overdueCount: number;
  paidThisMonth: number;
}

@Component({
  selector: 'fp-dashboard',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  template: `
    <div class="space-y-6">

      <!-- Page header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-slate-800">Dashboard</h2>
          <p class="text-slate-500 text-sm mt-0.5">Vue d'ensemble de votre activité financière</p>
        </div>
        <a routerLink="/invoices/new"
           class="inline-flex items-center gap-2 bg-blue-700 hover:bg-blue-800
                  text-white px-4 py-2 rounded-lg text-sm font-medium">
          + Nouvelle facture
        </a>
      </div>

      <!-- KPI cards -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">

        <div class="bg-white rounded-xl border border-slate-200 p-5">
          <p class="text-xs font-medium text-slate-500 uppercase tracking-wide">Total factures</p>
          <p class="text-3xl font-bold text-slate-800 mt-2">{{ recentInvoices().length }}</p>
          <p class="text-xs text-slate-400 mt-1">Ce mois</p>
        </div>

        <div class="bg-white rounded-xl border border-slate-200 p-5">
          <p class="text-xs font-medium text-slate-500 uppercase tracking-wide">En attente</p>
          <p class="text-3xl font-bold text-blue-700 mt-2">
            {{ sentCount() }}
          </p>
          <p class="text-xs text-slate-400 mt-1">Factures envoyées</p>
        </div>

        <div class="bg-white rounded-xl border border-slate-200 p-5">
          <p class="text-xs font-medium text-slate-500 uppercase tracking-wide">En retard</p>
          <p class="text-3xl font-bold text-red-600 mt-2">{{ overdueCount() }}</p>
          <p class="text-xs text-slate-400 mt-1">Nécessitent une relance</p>
        </div>

        <div class="bg-white rounded-xl border border-slate-200 p-5">
          <p class="text-xs font-medium text-slate-500 uppercase tracking-wide">Payées</p>
          <p class="text-3xl font-bold text-green-600 mt-2">{{ paidCount() }}</p>
          <p class="text-xs text-slate-400 mt-1">Ce mois</p>
        </div>

      </div>

      <!-- Recent invoices -->
      <div class="bg-white rounded-xl border border-slate-200">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="font-semibold text-slate-800">Factures récentes</h3>
          <a routerLink="/invoices" class="text-sm text-blue-600 hover:underline">Voir tout →</a>
        </div>

        @if (loading()) {
          <div class="p-8 text-center text-slate-400 text-sm">Chargement...</div>
        } @else if (recentInvoices().length === 0) {
          <div class="p-8 text-center text-slate-400 text-sm">
            Aucune facture. <a routerLink="/invoices/new" class="text-blue-600">Créer la première →</a>
          </div>
        } @else {
          <div class="divide-y divide-slate-100">
            @for (invoice of recentInvoices(); track invoice.id) {
              <div class="px-6 py-3 flex items-center justify-between hover:bg-slate-50">
                <div class="flex items-center gap-3">
                  <div>
                    <p class="text-sm font-medium text-slate-800">{{ invoice.invoiceNumber }}</p>
                    <p class="text-xs text-slate-500">{{ invoice.clientName }}</p>
                  </div>
                </div>
                <div class="flex items-center gap-4">
                  <span [class]="'px-2.5 py-0.5 rounded-full text-xs font-medium ' + statusColor(invoice.status)">
                    {{ statusLabel(invoice.status) }}
                  </span>
                  <p class="text-sm font-semibold text-slate-800">
                    {{ invoice.totalTtcAmount | number:'1.0-0' }} {{ invoice.currency }}
                  </p>
                </div>
              </div>
            }
          </div>
        }
      </div>

    </div>
  `,
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly loading = signal(true);
  readonly recentInvoices = signal<InvoiceSummary[]>([]);

  readonly sentCount = () => this.recentInvoices().filter(i => i.status === 'SENT').length;
  readonly overdueCount = () => this.recentInvoices().filter(i => i.status === 'OVERDUE').length;
  readonly paidCount = () => this.recentInvoices().filter(i => i.status === 'PAID').length;

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
