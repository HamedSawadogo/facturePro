import { Routes } from '@angular/router';

export const INVOICES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./invoice-list/invoice-list').then(m => m.InvoiceListComponent),
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./invoice-form/invoice-form').then(m => m.InvoiceFormComponent),
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./invoice-detail/invoice-detail').then(m => m.InvoiceDetailComponent),
  },
];
