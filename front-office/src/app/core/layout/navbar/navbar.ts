import { Component, ElementRef, HostListener, inject, signal } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { NotificationStore } from '../../notifications/notification.store';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'fp-navbar',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './navbar.html',
  host: { class: 'block flex-shrink-0' },
})
export class NavbarComponent {
  readonly auth = inject(AuthService);
  readonly notifStore = inject(NotificationStore);
  private readonly el = inject(ElementRef);

  readonly unreadCount = this.notifStore.unreadCount;
  readonly panelOpen = signal(false);

  get initial(): string {
    return this.auth.currentUser()?.fullName?.[0]?.toUpperCase() ?? '?';
  }

  togglePanel(): void {
    this.panelOpen.update(v => !v);
    if (this.panelOpen()) this.notifStore.markAllRead();
  }

  dismiss(id: string): void {
    this.notifStore.remove(id);
  }

  typeIcon(type: string): string {
    const icons: Record<string, string> = {
      'invoice.created': '📄',
      'invoice.sent': '📤',
      'payment.received': '💰',
    };
    return icons[type] ?? '🔔';
  }

  /** Ferme le panel si le clic est en dehors du composant */
  @HostListener('document:click', ['$event'])
  onDocClick(event: Event): void {
    if (!this.el.nativeElement.contains(event.target)) {
      this.panelOpen.set(false);
    }
  }
}
