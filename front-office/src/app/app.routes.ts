import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/shell/shell').then(m => m.ShellComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard').then(m => m.DashboardComponent),
      },
      {
        path: 'clients',
        loadChildren: () =>
          import('./features/clients/clients.routes').then(m => m.CLIENTS_ROUTES),
      },
      {
        path: 'invoices',
        loadChildren: () =>
          import('./features/invoices/invoices.routes').then(m => m.INVOICES_ROUTES),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
