import { Component, inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'fp-navbar',
  standalone: true,
  template: `
    <header class="bg-white border-b border-slate-200 px-6 py-3 flex items-center justify-between">
      <div></div>
      <div class="flex items-center gap-4">
        <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium
                     bg-blue-50 text-blue-700">
          {{ auth.currentUser()?.role }}
        </span>
        <div class="h-8 w-8 rounded-full bg-blue-700 text-white flex items-center justify-center text-sm font-bold">
          {{ initial }}
        </div>
      </div>
    </header>
  `,
})
export class NavbarComponent {
  readonly auth = inject(AuthService);

  get initial(): string {
    return this.auth.currentUser()?.fullName?.[0]?.toUpperCase() ?? '?';
  }
}
