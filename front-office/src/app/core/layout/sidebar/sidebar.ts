import { Component, inject, signal } from '@angular/core';
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
  template: `
    <aside class="w-64 bg-blue-900 text-white flex flex-col flex-shrink-0">

      <!-- Logo -->
      <div class="px-6 py-5 border-b border-blue-800">
        <h1 class="text-xl font-bold tracking-tight">
          <span class="text-amber-400">Facture</span>Pro Africa
        </h1>
        <p class="text-xs text-blue-300 mt-0.5">Gestion financière simplifiée</p>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        @for (item of navItems; track item.path) {
          <a
            [routerLink]="item.path"
            routerLinkActive="bg-blue-800 text-white"
            [routerLinkActiveOptions]="{ exact: item.path === '' }"
            class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-blue-200
                   hover:bg-blue-800 hover:text-white text-sm font-medium"
          >
            <span class="text-lg">{{ item.icon }}</span>
            {{ item.label }}
          </a>
        }
      </nav>

      <!-- User info -->
      <div class="px-4 py-4 border-t border-blue-800">
        <p class="text-sm font-medium text-white truncate">
          {{ auth.currentUser()?.fullName }}
        </p>
        <p class="text-xs text-blue-400 truncate">{{ auth.currentUser()?.email }}</p>
        <button
          (click)="auth.logout()"
          class="mt-2 w-full text-left text-xs text-blue-400 hover:text-white"
        >
          Déconnexion →
        </button>
      </div>
    </aside>
  `,
})
export class SidebarComponent {
  readonly auth = inject(AuthService);

  readonly navItems: NavItem[] = [
    { label: 'Dashboard', icon: '📊', path: '/dashboard' },
    { label: 'Factures', icon: '📄', path: '/invoices' },
    { label: 'Clients', icon: '👥', path: '/clients' },
    { label: 'Paiements', icon: '💳', path: '/payments' },
  ];
}
