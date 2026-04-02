import { Routes } from '@angular/router';

export const CLIENTS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./client-list/client-list').then(m => m.ClientListComponent),
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./client-form/client-form').then(m => m.ClientFormComponent),
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./client-form/client-form').then(m => m.ClientFormComponent),
  },
];
