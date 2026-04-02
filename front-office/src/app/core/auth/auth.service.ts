import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { AuthUser, LoginRequest } from '../models/auth.model';

const API = 'http://localhost:8080/api/v1/auth';
const STORAGE_KEY = 'fp_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly currentUser = signal<AuthUser | null>(this.loadFromStorage());

  login(request: LoginRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${API}/login`, request).pipe(
      tap(user => {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
        this.currentUser.set(user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/auth/login']);
  }

  get token(): string | null {
    return this.currentUser()?.accessToken ?? null;
  }

  get tenantId(): string | null {
    return this.currentUser()?.tenantId ?? null;
  }

  get isAuthenticated(): boolean {
    return this.currentUser() !== null;
  }

  private loadFromStorage(): AuthUser | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}
