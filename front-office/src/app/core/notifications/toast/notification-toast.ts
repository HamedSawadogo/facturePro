import { Component, inject, OnInit, signal } from '@angular/core';
import { AppNotification, NotificationStore } from '../notification.store';
import { SseService } from '../sse.service';
import { AuthService } from '../../auth/auth.service';

/**
 * Composant de notifications toast — s'affiche en overlay en bas à droite.
 * Démarre la connexion SSE dès que l'utilisateur est authentifié.
 */
@Component({
  selector: 'fp-notification-toast',
  standalone: true,
  template: `
    <div class="fixed bottom-5 right-5 z-50 flex flex-col gap-2 pointer-events-none" style="max-width: 340px;">
      @for (notif of visibleToasts(); track notif.id) {
        <div
          class="pointer-events-auto bg-white border border-slate-200 rounded-xl shadow-lg px-4 py-3
                 flex items-start gap-3 animate-slide-in"
          style="animation: slideIn 0.25s ease-out"
        >
          <span class="text-lg mt-0.5">{{ typeIcon(notif.type) }}</span>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-slate-800 leading-snug">{{ notif.message }}</p>
            <p class="text-xs text-slate-400 mt-0.5">{{ formatTime(notif.createdAt) }}</p>
          </div>
          <button
            (click)="dismiss(notif.id)"
            class="text-slate-300 hover:text-slate-500 flex-shrink-0 mt-0.5 text-xs"
          >✕</button>
        </div>
      }
    </div>
  `,
  styles: [`
    @keyframes slideIn {
      from { opacity: 0; transform: translateY(12px); }
      to   { opacity: 1; transform: translateY(0); }
    }
  `],
})
export class NotificationToastComponent implements OnInit {
  private readonly store = inject(NotificationStore);
  private readonly sse = inject(SseService);
  private readonly auth = inject(AuthService);

  readonly visibleToasts = signal<AppNotification[]>([]);
  private shownIds = new Set<string>();
  private prevCount = 0;

  ngOnInit(): void {
    if (this.auth.isAuthenticated) {
      this.sse.connect();
    }

    // Surveiller les nouvelles notifications
    setInterval(() => {
      const current = this.store.notifications();
      if (current.length > this.prevCount) {
        const newOnes = current.filter(n => !this.shownIds.has(n.id));
        for (const n of newOnes) {
          this.shownIds.add(n.id);
          this.visibleToasts.update(list => [n, ...list]);
          setTimeout(() => this.dismiss(n.id), 5000);
        }
        this.prevCount = current.length;
      }
    }, 300);
  }

  dismiss(id: string): void {
    this.visibleToasts.update(list => list.filter(n => n.id !== id));
  }

  typeIcon(type: string): string {
    const icons: Record<string, string> = {
      'invoice.created': '📄',
      'invoice.sent': '📤',
      'payment.received': '💰',
    };
    return icons[type] ?? '🔔';
  }

  formatTime(date: Date): string {
    return date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }
}
