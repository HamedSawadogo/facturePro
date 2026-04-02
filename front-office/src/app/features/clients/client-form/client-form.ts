import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/api/api.service';

@Component({
  selector: 'fp-client-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-2xl space-y-5">

      <!-- Header -->
      <div class="flex items-center gap-3">
        <a routerLink="/clients" class="text-slate-400 hover:text-slate-600">← Retour</a>
        <h2 class="text-2xl font-bold text-slate-800">
          {{ isEdit() ? 'Modifier le client' : 'Nouveau client' }}
        </h2>
      </div>

      <!-- Form card -->
      <div class="bg-white rounded-xl border border-slate-200 p-6">
        <form [formGroup]="form" (ngSubmit)="submit()" class="space-y-4">

          <div class="grid grid-cols-2 gap-4">
            <div class="col-span-2">
              <label class="fp-label">Nom *</label>
              <input type="text" formControlName="name" class="fp-input" placeholder="Entreprise Sawadogo SARL" />
            </div>

            <div>
              <label class="fp-label">Email</label>
              <input type="email" formControlName="email" class="fp-input" placeholder="contact@exemple.com" />
            </div>

            <div>
              <label class="fp-label">Téléphone (format +226...)</label>
              <input type="tel" formControlName="phone" class="fp-input" placeholder="+22670123456" />
            </div>

            <div class="col-span-2">
              <label class="fp-label">Type de client *</label>
              <select formControlName="clientType" class="fp-input">
                <option value="INDIVIDUAL">Particulier / Freelance</option>
                <option value="COMPANY">Entreprise / PME</option>
                <option value="GOVERNMENT">Organisme public</option>
              </select>
            </div>

            <div class="col-span-2">
              <label class="fp-label">Adresse</label>
              <input type="text" formControlName="address" class="fp-input" placeholder="01 BP 123, Secteur 10" />
            </div>

            <div>
              <label class="fp-label">Ville</label>
              <input type="text" formControlName="city" class="fp-input" placeholder="Ouagadougou" />
            </div>

            <div>
              <label class="fp-label">Pays</label>
              <input type="text" formControlName="country" class="fp-input" placeholder="Burkina Faso" />
            </div>

            <div class="col-span-2">
              <label class="fp-label">Numéro IFU / fiscal</label>
              <input type="text" formControlName="taxNumber" class="fp-input" placeholder="IFU-0000000" />
            </div>
          </div>

          @if (error()) {
            <p class="text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg">{{ error() }}</p>
          }

          <div class="flex gap-3 pt-2">
            <button
              type="submit"
              [disabled]="form.invalid || saving()"
              class="bg-blue-700 hover:bg-blue-800 disabled:bg-blue-300
                     text-white px-6 py-2.5 rounded-lg text-sm font-medium"
            >
              @if (saving()) { Enregistrement... } @else { Enregistrer }
            </button>
            <a routerLink="/clients"
               class="px-6 py-2.5 rounded-lg text-sm font-medium border border-slate-300 hover:bg-slate-50">
              Annuler
            </a>
          </div>

        </form>
      </div>

    </div>
  `,
  styles: [`
    .fp-label { @apply block text-sm font-medium text-slate-700 mb-1; }
    .fp-input { @apply w-full px-3 py-2 rounded-lg border border-slate-300 text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500; }
  `],
})
export class ClientFormComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  readonly isEdit = signal(false);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);

  private clientId: string | null = null;

  readonly form = this.fb.group({
    name: ['', Validators.required],
    email: [''],
    phone: [''],
    clientType: ['COMPANY', Validators.required],
    address: [''],
    city: [''],
    country: ['Burkina Faso'],
    taxNumber: [''],
  });

  ngOnInit(): void {
    this.clientId = this.route.snapshot.paramMap.get('id');
    if (this.clientId) {
      this.isEdit.set(true);
      this.api.getClient(this.clientId).subscribe(client => {
        this.form.patchValue(client as any);
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.saving.set(true);
    this.error.set(null);

    const body = this.form.getRawValue() as any;
    const request$ = this.isEdit()
      ? this.api.updateClient(this.clientId!, body)
      : this.api.createClient(body);

    request$.subscribe({
      next: () => this.router.navigate(['/clients']),
      error: () => {
        this.error.set("Une erreur s'est produite. Veuillez réessayer.");
        this.saving.set(false);
      },
    });
  }
}
