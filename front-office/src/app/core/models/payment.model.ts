export type PaymentMethod =
  | 'ORANGE_MONEY'
  | 'MTN_MOMO'
  | 'MOOV_MONEY'
  | 'BANK_TRANSFER'
  | 'CASH'
  | 'CHEQUE'
  | 'CARD';

export type PaymentStatus = 'PENDING' | 'CONFIRMED' | 'FAILED' | 'REFUNDED';

export interface PaymentSummary {
  id: string;
  invoiceId: string;
  amount: number;
  currency: string;
  method: PaymentMethod;
  status: PaymentStatus;
  paymentDate: string;
  reference?: string;
  notes?: string;
  createdAt: string;
}

export interface RecordPaymentRequest {
  invoiceId: string;
  amount: number;
  currency: string;
  method: PaymentMethod;
  paymentDate: string;
  reference?: string;
  notes?: string;
}

export const METHOD_LABELS: Record<PaymentMethod, string> = {
  ORANGE_MONEY: 'Orange Money',
  MTN_MOMO: 'MTN MoMo',
  MOOV_MONEY: 'Moov Money',
  BANK_TRANSFER: 'Virement bancaire',
  CASH: 'Espèces',
  CHEQUE: 'Chèque',
  CARD: 'Carte bancaire',
};

export const METHOD_ICONS: Record<PaymentMethod, string> = {
  ORANGE_MONEY: '🟠',
  MTN_MOMO: '🟡',
  MOOV_MONEY: '🔵',
  BANK_TRANSFER: '🏦',
  CASH: '💵',
  CHEQUE: '📝',
  CARD: '💳',
};

export const STATUS_COLORS_PAY: Record<PaymentStatus, string> = {
  PENDING: 'bg-yellow-100 text-yellow-700',
  CONFIRMED: 'bg-green-100 text-green-700',
  FAILED: 'bg-red-100 text-red-700',
  REFUNDED: 'bg-slate-100 text-slate-600',
};

export const STATUS_LABELS_PAY: Record<PaymentStatus, string> = {
  PENDING: 'En attente',
  CONFIRMED: 'Confirmé',
  FAILED: 'Échoué',
  REFUNDED: 'Remboursé',
};
