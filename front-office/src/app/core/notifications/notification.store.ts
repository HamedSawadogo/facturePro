import { computed, Injectable, signal } from '@angular/core';

export interface AppNotification {
  id: string;
  type: 'invoice.created' | 'invoice.sent' | 'payment.received' | string;
  message: string;
  read: boolean;
  createdAt: Date;
}

@Injectable({ providedIn: 'root' })
export class NotificationStore {
  private readonly _notifications = signal<AppNotification[]>([]);

  readonly notifications = this._notifications.asReadonly();
  readonly unreadCount = computed(() => this._notifications().filter(n => !n.read).length);
  readonly recent = computed(() => this._notifications().slice(0, 5));

  push(type: string, message: string): void {
    const notif: AppNotification = {
      id: crypto.randomUUID(),
      type,
      message,
      read: false,
      createdAt: new Date(),
    };
    this._notifications.update(list => [notif, ...list].slice(0, 50));
  }

  markAllRead(): void {
    this._notifications.update(list => list.map(n => ({ ...n, read: true })));
  }

  remove(id: string): void {
    this._notifications.update(list => list.filter(n => n.id !== id));
  }

  clearAll(): void {
    this._notifications.set([]);
  }
}
