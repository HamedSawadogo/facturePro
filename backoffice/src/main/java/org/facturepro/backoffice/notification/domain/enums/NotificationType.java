package org.facturepro.backoffice.notification.domain.enums;

public enum NotificationType {
    INVOICE_CREATED,
    INVOICE_SENT,
    PAYMENT_CONFIRMED,
    REMINDER_SOFT,      // J+1
    REMINDER_FIRM,      // J+7
    REMINDER_ESCALATED  // J+14
}
