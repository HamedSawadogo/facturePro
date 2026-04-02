export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  tenantId: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: 'ADMIN' | 'ACCOUNTANT' | 'VIEWER';
}

export interface AuthUser {
  userId: string;
  tenantId: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'ACCOUNTANT' | 'VIEWER';
  accessToken: string;
  refreshToken: string;
}
