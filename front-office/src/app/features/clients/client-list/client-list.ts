import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/api/api.service';
import { Client } from '../../../core/models/client.model';

@Component({
  selector: 'fp-client-list',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './client-list.html',
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
