import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

interface NavItem {
  label: string;
  icon: string;
  path: string;
}

@Component({
  selector: 'fp-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  host: { class: 'flex h-full' },
})
export class SidebarComponent {
  readonly auth = inject(AuthService);

  readonly mainNavItems: NavItem[] = [
    { label: 'Dashboard', icon: '📊', path: '/dashboard' },
    { label: 'Factures', icon: '📄', path: '/invoices' },
    { label: 'Clients', icon: '👥', path: '/clients' },
    { label: 'Paiements', icon: '💳', path: '/payments' },
  ];

  readonly bottomNavItems: NavItem[] = [
    { label: 'Paramètres', icon: '⚙️', path: '/settings' },
  ];

  get initial(): string {
    return this.auth.currentUser()?.fullName?.[0]?.toUpperCase() ?? '?';
  }
}
