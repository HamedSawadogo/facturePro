import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar';
import { NavbarComponent } from '../navbar/navbar';

@Component({
  selector: 'fp-shell',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex h-screen overflow-hidden bg-slate-50">
      <!-- Sidebar -->
      <fp-sidebar />

      <!-- Main content -->
      <div class="flex flex-col flex-1 overflow-hidden">
        <fp-navbar />
        <main class="flex-1 overflow-y-auto p-6">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
})
export class ShellComponent {}
