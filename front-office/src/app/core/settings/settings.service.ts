import { Injectable, signal } from '@angular/core';

export interface TenantSettings {
  companyName: string;
  logoBase64: string;
  country: string;
  currency: string;
  address: string;
  phone: string;
  email: string;
  taxNumber: string;
  invoiceFooter: string;
}

const STORAGE_KEY = 'fp_settings';

const DEFAULTS: TenantSettings = {
  companyName: 'Mon Entreprise',
  logoBase64: '',
  country: 'Burkina Faso',
  currency: 'XOF',
  address: '',
  phone: '',
  email: '',
  taxNumber: '',
  invoiceFooter: 'Merci pour votre confiance.',
};

@Injectable({ providedIn: 'root' })
export class SettingsService {
  readonly settings = signal<TenantSettings>(this.load());

  save(patch: Partial<TenantSettings>): void {
    const updated = { ...this.settings(), ...patch };
    this.settings.set(updated);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
  }

  private load(): TenantSettings {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? { ...DEFAULTS, ...JSON.parse(raw) } : { ...DEFAULTS };
    } catch {
      return { ...DEFAULTS };
    }
  }
}
