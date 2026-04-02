export type InvoiceStatus = 'DRAFT' | 'SENT' | 'PARTIALLY_PAID' | 'PAID' | 'OVERDUE' | 'CANCELLED';
export type InvoiceType = 'INVOICE' | 'QUOTE' | 'CREDIT_NOTE';

export interface InvoiceSummary {
  id: string;
  invoiceNumber: string;
  type: InvoiceType;
  status: InvoiceStatus;
  clientId: string;
  clientName: string;
  issueDate: string;
  dueDate: string;
  totalHtAmount: number;
  totalTtcAmount: number;
  currency: string;
  amountPaidAmount: number;
  notes?: string;
}

export interface InvoiceItemRequest {
  description: string;
  quantity: number;
  unitPriceAmount: number;
  currency: string;
  taxRate: number;
}

export interface CreateInvoiceRequest {
  type: InvoiceType;
  clientId: string;
  clientName: string;
  issueDate: string;
  dueDate: string;
  items: InvoiceItemRequest[];
  notes?: string;
  paymentTerms?: string;
}

export const STATUS_LABELS: Record<InvoiceStatus, string> = {
  DRAFT: 'Brouillon',
  SENT: 'Envoyée',
  PARTIALLY_PAID: 'Part. payée',
  PAID: 'Payée',
  OVERDUE: 'En retard',
  CANCELLED: 'Annulée',
};

export const STATUS_COLORS: Record<InvoiceStatus, string> = {
  DRAFT: 'bg-gray-100 text-gray-700',
  SENT: 'bg-blue-100 text-blue-700',
  PARTIALLY_PAID: 'bg-yellow-100 text-yellow-700',
  PAID: 'bg-green-100 text-green-700',
  OVERDUE: 'bg-red-100 text-red-700',
  CANCELLED: 'bg-gray-100 text-gray-500',
};
