import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { SettingsService } from '../../core/settings/settings.service';

@Component({
  selector: 'fp-settings',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './settings.html',
  styles: [`
    .fp-label { @apply block text-sm font-medium text-slate-700 mb-1; }
    .fp-input { @apply w-full px-3 py-2 rounded-lg border border-slate-300 text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500; }
  `],
})
export class SettingsComponent {
  private readonly svc = inject(SettingsService);
  private readonly fb = inject(FormBuilder);

  readonly saved = signal(false);
  readonly logoPreview = signal<string>(this.svc.settings().logoBase64);

  readonly countries = [
    { value: 'Burkina Faso',    label: 'Burkina Faso',    flag: '🇧🇫' },
    { value: 'Côte d\'Ivoire',  label: 'Côte d\'Ivoire',  flag: '🇨🇮' },
    { value: 'Mali',            label: 'Mali',            flag: '🇲🇱' },
    { value: 'Sénégal',         label: 'Sénégal',         flag: '🇸🇳' },
    { value: 'Niger',           label: 'Niger',           flag: '🇳🇪' },
    { value: 'Guinée',          label: 'Guinée',          flag: '🇬🇳' },
    { value: 'Togo',            label: 'Togo',            flag: '🇹🇬' },
    { value: 'Bénin',           label: 'Bénin',           flag: '🇧🇯' },
    { value: 'Cameroun',        label: 'Cameroun',        flag: '🇨🇲' },
    { value: 'Congo',           label: 'Congo',           flag: '🇨🇬' },
    { value: 'Gabon',           label: 'Gabon',           flag: '🇬🇦' },
    { value: 'Maroc',           label: 'Maroc',           flag: '🇲🇦' },
    { value: 'Algérie',         label: 'Algérie',         flag: '🇩🇿' },
    { value: 'Tunisie',         label: 'Tunisie',         flag: '🇹🇳' },
  ];

  readonly currencies = [
    { value: 'XOF', label: 'Franc CFA UEMOA' },
    { value: 'XAF', label: 'Franc CFA CEMAC' },
    { value: 'GNF', label: 'Franc guinéen' },
    { value: 'MAD', label: 'Dirham marocain' },
    { value: 'DZD', label: 'Dinar algérien' },
    { value: 'TND', label: 'Dinar tunisien' },
    { value: 'USD', label: 'Dollar américain' },
    { value: 'EUR', label: 'Euro' },
  ];

  readonly form = this.fb.group({
    companyName:   [this.svc.settings().companyName],
    address:       [this.svc.settings().address],
    phone:         [this.svc.settings().phone],
    email:         [this.svc.settings().email],
    taxNumber:     [this.svc.settings().taxNumber],
    country:       [this.svc.settings().country],
    currency:      [this.svc.settings().currency],
    invoiceFooter: [this.svc.settings().invoiceFooter],
  });

  onLogoChange(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    if (file.size > 500_000) { alert('Le fichier dépasse 500 ko'); return; }
    const reader = new FileReader();
    reader.onload = () => this.logoPreview.set(reader.result as string);
    reader.readAsDataURL(file);
  }

  removeLogo(): void {
    this.logoPreview.set('');
  }

  submit(): void {
    this.svc.save({
      ...this.form.getRawValue() as any,
      logoBase64: this.logoPreview(),
    });
    this.saved.set(true);
    setTimeout(() => this.saved.set(false), 3000);
  }
}
