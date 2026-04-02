import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client, CreateClientRequest, PageResponse } from '../models/client.model';
import { CreateInvoiceRequest, InvoiceSummary } from '../models/invoice.model';


@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl:string = 'http://localhost:8080/api/v1';

  // ── Clients ──────────────────────────────────────────────────
  getClients(search = '', page = 0, size = 20): Observable<PageResponse<Client>> {
    const params = new HttpParams()
      .set('search', search)
      .set('page', page)
      .set('size', size);
    return this.http.get<PageResponse<Client>>(`${this.baseUrl}/clients`, { params });
  }

  getClient(id: string): Observable<Client> {
    return this.http.get<Client>(`${this.baseUrl}/clients/${id}`);
  }

  createClient(body: CreateClientRequest): Observable<{ id: string }> {
    return this.http.post<{ id: string }>(`${this.baseUrl}/clients`, body);
  }

  updateClient(id: string, body: Partial<CreateClientRequest>): Observable<{ id: string }> {
    return this.http.put<{ id: string }>(`${this.baseUrl}/clients/${id}`, body);
  }

  // ── Invoices ─────────────────────────────────────────────────
  getInvoices(status?: string, clientId?: string, page = 0, size = 20): Observable<PageResponse<InvoiceSummary>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    if (clientId) params = params.set('clientId', clientId);
    return this.http.get<PageResponse<InvoiceSummary>>(`${this.baseUrl}/invoices`, { params });
  }

  getInvoice(id: string): Observable<InvoiceSummary> {
    return this.http.get<InvoiceSummary>(`${this.baseUrl}/invoices/${id}`);
  }

  createInvoice(body: CreateInvoiceRequest, idempotencyKey: string): Observable<{ id: string }> {
    return this.http.post<{ id: string }>(`${this.baseUrl}/invoices`, body, {
      headers: { 'X-Idempotency-Key': idempotencyKey },
    });
  }

  sendInvoice(id: string): Observable<{ id: string }> {
    return this.http.post<{ id: string }>(`${this.baseUrl}/invoices/${id}/send`, {});
  }
}
