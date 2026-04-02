export type ClientType = 'INDIVIDUAL' | 'COMPANY' | 'GOVERNMENT';

export interface Client {
  id: string;
  name: string;
  email: string | null;
  phone: string | null;
  clientType: ClientType;
  city: string | null;
  country: string | null;
  active: boolean;
}

export interface CreateClientRequest {
  name: string;
  email?: string;
  phone?: string;
  clientType: ClientType;
  address?: string;
  city?: string;
  country?: string;
  taxNumber?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
