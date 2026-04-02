import { Component, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/api/api.service';
import { DecimalPipe } from '@angular/common';
import { Client } from '../../../core/models/client.model';

@Component({
  selector: 'fp-invoice-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, DecimalPipe],
  templateUrl: './invoice-form.html',
  styles: [`
    .fp-label { @apply block text-sm font-medium text-slate-700 mb-1; }
    .fp-input { @apply w-full px-3 py-2 rounded-lg border border-slate-300 text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500; }
  `],
})
export class InvoiceFormComponent {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly clients = signal<Client[]>([]);

  constructor() {
    this.api.getClients('', 0, 100).subscribe({
      next: page => this.clients.set(page.content),
    });
  }

  onClientChange(event: Event): void {
    const id = (event.target as HTMLSelectElement).value;
    const client = this.clients().find(c => c.id === id) ?? null;
    this.form.patchValue({
      clientId: client?.id ?? '',
      clientName: client?.name ?? '',
    });
  }

  readonly form = this.fb.group({
    type: ['INVOICE', Validators.required],
    clientId: ['', Validators.required],
    clientName: ['', Validators.required],
    issueDate: [new Date().toISOString().split('T')[0], Validators.required],
    dueDate: ['', Validators.required],
    notes: [''],
    items: this.fb.array([this.createItemGroup()]),
  });

  get items(): FormArray {
    return this.form.get('items') as FormArray;
  }

  addItem(): void {
    this.items.push(this.createItemGroup());
  }

  removeItem(i: number): void {
    this.items.removeAt(i);
  }

  totalHt(): number {
    return this.items.controls.reduce((sum, ctrl) => {
      const qty = +ctrl.get('quantity')?.value || 0;
      const price = +ctrl.get('unitPriceAmount')?.value || 0;
      return sum + qty * price;
    }, 0);
  }

  totalTtc(): number {
    return this.items.controls.reduce((sum, ctrl) => {
      const qty = +ctrl.get('quantity')?.value || 0;
      const price = +ctrl.get('unitPriceAmount')?.value || 0;
      const tax = +ctrl.get('taxRate')?.value || 0;
      const ht = qty * price;
      return sum + ht + ht * (tax / 100);
    }, 0);
  }

  submit(): void {
    if (this.form.invalid) return;
    this.saving.set(true);
    this.error.set(null);

    const val = this.form.getRawValue();
    const body: any = {
      type: val.type,
      clientId: this.form.get("clientId")?.value,
      clientName: val.clientName,
      issueDate: val.issueDate,
      dueDate: val.dueDate,
      notes: val.notes,
      items: val.items?.map((i: any) => ({ ...i, currency: 'XOF' })),
    };

    this.api.createInvoice(body, crypto.randomUUID()).subscribe({
      next: res => this.router.navigate(['/invoices', res.id]),
      error: () => {
        this.error.set("Erreur lors de la création. Vérifiez les informations.");
        this.saving.set(false);
      },
    });
  }

  private createItemGroup() {
    return this.fb.group({
      description: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(0.01)]],
      unitPriceAmount: [0, [Validators.required, Validators.min(0)]],
      taxRate: [0],
    });
  }
}
