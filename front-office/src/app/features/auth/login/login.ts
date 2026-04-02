import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import {LoginRequest} from '../../../core/models/auth.model';

@Component({
  selector: 'fp-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-blue-900 to-blue-700
                flex items-center justify-center p-4">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-8">

        <!-- Header -->
        <div class="text-center mb-8">
          <h1 class="text-2xl font-bold text-blue-900">
            <span class="text-amber-500">Facture</span>Pro Africa
          </h1>
          <p class="text-slate-500 mt-1 text-sm">Connectez-vous à votre espace</p>
        </div>

        <!-- Form -->
        <form [formGroup]="form" (ngSubmit)="submit()" class="space-y-4">

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Email</label>
            <input
              type="email"
              formControlName="email"
              placeholder="vous@entreprise.com"
              class="w-full px-4 py-2.5 rounded-lg border border-slate-300
                     focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Mot de passe</label>
            <input
              type="password"
              formControlName="password"
              placeholder="••••••••"
              class="w-full px-4 py-2.5 rounded-lg border border-slate-300
                     focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            />
          </div>

          @if (error()) {
            <p class="text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg">
              {{ error() }}
            </p>
          }

          <button
            type="submit"
            [disabled]="form.invalid || loading()"
            class="w-full bg-blue-700 hover:bg-blue-800 disabled:bg-blue-300
                   text-white font-semibold py-2.5 rounded-lg text-sm"
          >
            @if (loading()) { Connexion... } @else { Se connecter }
          </button>
        </form>

      </div>
    </div>
  `,
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set(null);

    this.auth.login(this.form.getRawValue() as any).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => {
        this.error.set('Email ou mot de passe incorrect');
        this.loading.set(false);
      },
    });
  }
}
