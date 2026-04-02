import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/api/api.service';

@Component({
  selector: 'fp-client-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './client-form.html',
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
