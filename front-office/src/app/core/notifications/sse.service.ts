import { inject, Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { NotificationStore } from './notification.store';

/**
 * Se connecte au flux SSE du backend et pousse les événements dans NotificationStore.
 * Reconnexion automatique toutes les 5s en cas de déconnexion.
 */
@Injectable({ providedIn: 'root' })
export class SseService implements OnDestroy {
  private readonly auth = inject(AuthService);
  private readonly store = inject(NotificationStore);

  private eventSource: EventSource | null = null;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;

  connect(): void {
    const token = this.auth.token;
    if (!token) return;
    this.close();

    const url = `http://localhost:8080/api/v1/notifications/stream?token=${encodeURIComponent(token)}`;
    this.eventSource = new EventSource(url);

    this.eventSource.addEventListener('invoice.created', (e: Event) => {
      const data:any  = this.parse((e as MessageEvent).data);
      this.store.push('invoice.created', data?.message ?? 'Nouvelle facture créée');
    });

    this.eventSource.addEventListener('invoice.sent', (e: Event) => {
      const data: any = this.parse((e as MessageEvent).data);
      this.store.push('invoice.sent', data?.message ?? 'Facture envoyée');
    });

    this.eventSource.addEventListener('payment.received', (e: Event) => {
      const data: any  = this.parse((e as MessageEvent).data);
      this.store.push('payment.received', data?.message ?? 'Paiement reçu');
    });

    this.eventSource.onerror = () => {
      this.close();
      this.reconnectTimer = setTimeout(() => this.connect(), 5000);
    };
  }

  close(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }

  ngOnDestroy(): void {
    this.close();
  }

  private parse(data: string): Record<string, string> | null {
    try { return JSON.parse(data); } catch { return null; }
  }
}
