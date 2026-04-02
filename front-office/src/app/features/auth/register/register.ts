import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'fp-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styles: [`
    .fp-label { @apply block text-sm font-medium text-slate-700 mb-1; }
    .fp-input { @apply w-full px-3 py-2.5 rounded-lg border border-slate-300 text-sm
                       focus:outline-none focus:ring-2 focus:ring-blue-500 transition-shadow; }
  `],
})
export class RegisterComponent {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set(null);

    const val = this.form.getRawValue();
    const tenantId = crypto.randomUUID(); // Chaque inscription = nouveau tenant

    this.http.post('http://localhost:8080/api/v1/auth/register', {
      tenantId,
      email: val.email,
      password: val.password,
      firstName: val.firstName,
      lastName: val.lastName,
      role: 'ADMIN',
    }).subscribe({
      next: () => {
        // Auto-login après inscription
        this.auth.login({ email: val.email!, password: val.password! }).subscribe({
          next: () => this.router.navigate(['/dashboard']),
          error: () => this.router.navigate(['/auth/login']),
        });
      },
      error: (err) => {
        const msg = err?.error?.message ?? 'Erreur lors de la création du compte.';
        this.error.set(msg);
        this.loading.set(false);
      },
    });
  }
}
