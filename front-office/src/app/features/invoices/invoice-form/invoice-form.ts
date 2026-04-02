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
  template: `
    <div class="max-w-3xl space-y-5">

      <div class="flex items-center gap-3">
        <a routerLink="/invoices" class="text-slate-400 hover:text-slate-600">← Retour</a>
        <h2 class="text-2xl font-bold text-slate-800">Nouvelle facture</h2>
      </div>

      <form [formGroup]="form" (ngSubmit)="submit()" class="space-y-5">

        <!-- Infos générales -->
        <div class="bg-white rounded-xl border border-slate-200 p-6 space-y-4">
          <h3 class="font-semibold text-slate-800">Informations générales</h3>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="fp-label">Type *</label>
              <select formControlName="type" class="fp-input">
                <option value="INVOICE">Facture</option>
                <option value="QUOTE">Devis</option>
              </select>
            </div>

            <div>
              <label class="fp-label">Client *</label>
              <select class="fp-input" (change)="onClientChange($event)">
                <option value="">-- Sélectionner un client --</option>
                @for (c of clients(); track c.id) {
                  <option [value]="c.id">{{ c.name }}</option>
                }
              </select>
              @if (form.get('clientId')?.value === '') {
                <p class="text-xs text-red-500 mt-1">Veuillez sélectionner un client</p>
              }
            </div>

            <div>
              <label class="fp-label">Date d'émission *</label>
              <input type="date" formControlName="issueDate" class="fp-input" />
            </div>

            <div>
              <label class="fp-label">Date d'échéance *</label>
              <input type="date" formControlName="dueDate" class="fp-input" />
            </div>
          </div>
        </div>

        <!-- Lignes -->
        <div class="bg-white rounded-xl border border-slate-200 p-6 space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="font-semibold text-slate-800">Lignes de facturation</h3>
            <button type="button" (click)="addItem()"
                    class="text-sm text-blue-600 hover:underline">+ Ajouter une ligne</button>
          </div>

          <div formArrayName="items" class="space-y-3">
            @for (item of items.controls; track $index; let i = $index) {
              <div [formGroupName]="i" class="grid grid-cols-12 gap-2 items-end">
                <div class="col-span-5">
                  @if (i === 0) { <label class="fp-label">Description</label> }
                  <input type="text" formControlName="description" class="fp-input"
                         placeholder="Prestation de service" />
                </div>
                <div class="col-span-2">
                  @if (i === 0) { <label class="fp-label">Qté</label> }
                  <input type="number" formControlName="quantity" class="fp-input text-right"
                         placeholder="1" min="0.01" step="0.01" />
                </div>
                <div class="col-span-3">
                  @if (i === 0) { <label class="fp-label">Prix unitaire (XOF)</label> }
                  <input type="number" formControlName="unitPriceAmount" class="fp-input text-right"
                         placeholder="50000" min="0" />
                </div>
                <div class="col-span-1">
                  @if (i === 0) { <label class="fp-label">TVA%</label> }
                  <input type="number" formControlName="taxRate" class="fp-input text-right"
                         placeholder="0" min="0" max="100" />
                </div>
                <div class="col-span-1 flex items-end pb-0.5">
                  @if (items.length > 1) {
                    <button type="button" (click)="removeItem(i)"
                            class="text-red-400 hover:text-red-600 text-lg">✕</button>
                  }
                </div>
              </div>
            }
          </div>

          <!-- Total -->
          <div class="border-t border-slate-100 pt-3 flex justify-end">
            <div class="text-right space-y-1">
              <p class="text-sm text-slate-600">Total HT: <span class="font-semibold">{{ totalHt() | number:'1.0-0' }} XOF</span></p>
              <p class="text-base font-bold text-slate-800">Total TTC: {{ totalTtc() | number:'1.0-0' }} XOF</p>
            </div>
          </div>
        </div>

        <!-- Notes -->
        <div class="bg-white rounded-xl border border-slate-200 p-6">
          <label class="fp-label">Notes / Conditions de paiement</label>
          <textarea formControlName="notes" rows="3" class="fp-input resize-none"
                    placeholder="Paiement à 30 jours..."></textarea>
        </div>

        @if (error()) {
          <p class="text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg">{{ error() }}</p>
        }

        <div class="flex gap-3">
          <button type="submit" [disabled]="form.invalid || saving()"
                  class="bg-blue-700 hover:bg-blue-800 disabled:bg-blue-300
                         text-white px-6 py-2.5 rounded-lg text-sm font-medium">
            @if (saving()) { Enregistrement... } @else { Créer la facture }
          </button>
          <a routerLink="/invoices"
             class="px-6 py-2.5 rounded-lg text-sm font-medium border border-slate-300 hover:bg-slate-50">
            Annuler
          </a>
        </div>

      </form>
    </div>
  `,
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
