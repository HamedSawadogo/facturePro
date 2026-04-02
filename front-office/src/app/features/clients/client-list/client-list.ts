import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/api/api.service';
import { Client } from '../../../core/models/client.model';

@Component({
  selector: 'fp-client-list',
  standalone: true,
  imports: [RouterLink, FormsModule],
  template: `
    <div class="space-y-5">

      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-slate-800">Clients</h2>
          <p class="text-slate-500 text-sm mt-0.5">{{ total() }} client(s) enregistré(s)</p>
        </div>
        <a routerLink="new"
           class="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded-lg text-sm font-medium">
          + Nouveau client
        </a>
      </div>

      <!-- Search -->
      <div class="relative">
        <span class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">🔍</span>
        <input
          type="text"
          [(ngModel)]="searchQuery"
          (ngModelChange)="onSearch($event)"
          placeholder="Rechercher par nom ou email..."
          class="w-full pl-9 pr-4 py-2.5 rounded-lg border border-slate-300
                 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm bg-white"
        />
      </div>

      <!-- Table -->
      <div class="bg-white rounded-xl border border-slate-200 overflow-hidden">
        @if (loading()) {
          <div class="p-10 text-center text-slate-400 text-sm">Chargement...</div>
        } @else if (clients().length === 0) {
          <div class="p-10 text-center text-slate-400 text-sm">
            Aucun client trouvé.
            <a routerLink="new" class="text-blue-600 hover:underline ml-1">Créer le premier →</a>
          </div>
        } @else {
          <table class="w-full text-sm">
            <thead class="bg-slate-50 border-b border-slate-200">
              <tr>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Nom</th>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Email</th>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Téléphone</th>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Ville</th>
                <th class="text-left px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Type</th>
                <th class="px-6 py-3"></th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              @for (client of clients(); track client.id) {
                <tr class="hover:bg-slate-50">
                  <td class="px-6 py-3 font-medium text-slate-800">{{ client.name }}</td>
                  <td class="px-6 py-3 text-slate-600">{{ client.email ?? '—' }}</td>
                  <td class="px-6 py-3 text-slate-600">{{ client.phone ?? '—' }}</td>
                  <td class="px-6 py-3 text-slate-600">{{ client.city ?? '—' }}</td>
                  <td class="px-6 py-3">
                    <span class="px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-700">
                      {{ typeLabel(client.clientType) }}
                    </span>
                  </td>
                  <td class="px-6 py-3 text-right">
                    <a [routerLink]="[client.id, 'edit']"
                       class="text-blue-600 hover:underline text-xs">Modifier</a>
                  </td>
                </tr>
              }
            </tbody>
          </table>

          <!-- Pagination -->
          @if (totalPages() > 1) {
            <div class="px-6 py-3 border-t border-slate-100 flex items-center justify-between">
              <p class="text-xs text-slate-500">Page {{ currentPage() + 1 }} / {{ totalPages() }}</p>
              <div class="flex gap-2">
                <button
                  [disabled]="currentPage() === 0"
                  (click)="loadPage(currentPage() - 1)"
                  class="px-3 py-1.5 text-xs border border-slate-300 rounded-lg disabled:opacity-40 hover:bg-slate-50"
                >← Préc.</button>
                <button
                  [disabled]="currentPage() === totalPages() - 1"
                  (click)="loadPage(currentPage() + 1)"
                  class="px-3 py-1.5 text-xs border border-slate-300 rounded-lg disabled:opacity-40 hover:bg-slate-50"
                >Suiv. →</button>
              </div>
            </div>
          }
        }
      </div>

    </div>
  `,
})
export class ClientListComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly loading = signal(true);
  readonly clients = signal<Client[]>([]);
  readonly total = signal(0);
  readonly totalPages = signal(0);
  readonly currentPage = signal(0);

  searchQuery = '';
  private searchTimeout: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.loading.set(true);
    this.api.getClients(this.searchQuery, page).subscribe({
      next: res => {
        this.clients.set(res.content);
        this.total.set(res.totalElements);
        this.totalPages.set(res.totalPages);
        this.currentPage.set(res.page);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  onSearch(query: string): void {
    if (this.searchTimeout) clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => this.loadPage(0), 350);
  }

  typeLabel(type: string): string {
    const labels: Record<string, string> = {
      INDIVIDUAL: 'Particulier',
      COMPANY: 'Entreprise',
      GOVERNMENT: 'Organisme',
    };
    return labels[type] ?? type;
  }
}
