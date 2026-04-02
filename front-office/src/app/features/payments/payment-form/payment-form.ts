import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/api/api.service';
import { METHOD_ICONS, METHOD_LABELS, PaymentMethod } from '../../../core/models/payment.model';

@Component({
  selector: 'fp-payment-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './payment-form.html',
  styles: [`
    .fp-label { @apply block text-sm font-medium text-slate-700 mb-1; }
    .fp-input { @apply w-full px-3 py-2 rounded-lg border border-slate-300 text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500; }
  `],
})
export class PaymentFormComponent {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal(false);

  readonly paymentMethods: { value: PaymentMethod; label: string; icon: string }[] = [
    { value: 'ORANGE_MONEY', label: 'Orange Money', icon: METHOD_ICONS['ORANGE_MONEY'] },
    { value: 'MTN_MOMO', label: 'MTN MoMo', icon: METHOD_ICONS['MTN_MOMO'] },
    { value: 'MOOV_MONEY', label: 'Moov Money', icon: METHOD_ICONS['MOOV_MONEY'] },
    { value: 'BANK_TRANSFER', label: 'Virement', icon: METHOD_ICONS['BANK_TRANSFER'] },
    { value: 'CASH', label: 'Espèces', icon: METHOD_ICONS['CASH'] },
    { value: 'CHEQUE', label: 'Chèque', icon: METHOD_ICONS['CHEQUE'] },
  ];

  readonly form = this.fb.group({
    invoiceId: ['', Validators.required],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    paymentDate: [new Date().toISOString().split('T')[0], Validators.required],
    method: ['ORANGE_MONEY' as PaymentMethod, Validators.required],
    reference: [''],
    notes: [''],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.saving.set(true);
    this.error.set(null);
    this.success.set(false);

    const val = this.form.getRawValue();
    this.api.recordPayment(
      {
        invoiceId: val.invoiceId!,
        amount: val.amount!,
        currency: 'XOF',
        method: val.method!,
        paymentDate: val.paymentDate!,
        reference: val.reference || undefined,
        notes: val.notes || undefined,
      },
      crypto.randomUUID()
    ).subscribe({
      next: () => {
        this.success.set(true);
        this.saving.set(false);
        this.form.reset({
          paymentDate: new Date().toISOString().split('T')[0],
          method: 'ORANGE_MONEY',
        });
        setTimeout(() => this.router.navigate(['/payments']), 1500);
      },
      error: () => {
        this.error.set("Erreur lors de l'enregistrement. Vérifiez les informations.");
        this.saving.set(false);
      },
    });
  }
}
