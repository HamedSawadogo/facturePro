import { Routes } from '@angular/router';

export const PAYMENTS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./payment-list/payment-list').then(m => m.PaymentListComponent),
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./payment-form/payment-form').then(m => m.PaymentFormComponent),
  },
];
