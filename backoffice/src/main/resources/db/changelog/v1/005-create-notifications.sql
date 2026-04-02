--liquibase formatted sql

--changeset hamed:005-create-notifications
CREATE TABLE notification_logs (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL,
    invoice_id      UUID,
    recipient       VARCHAR(255) NOT NULL,
    channel         VARCHAR(20)  NOT NULL,
    type            VARCHAR(30)  NOT NULL,
    subject         VARCHAR(255),
    success         BOOLEAN      NOT NULL DEFAULT FALSE,
    error_message   TEXT,
    sent_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    -- Audit trail
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_notification_logs PRIMARY KEY (id),
    CONSTRAINT chk_notif_channel CHECK (channel IN ('EMAIL', 'SMS', 'WHATSAPP')),
    CONSTRAINT chk_notif_type    CHECK (type IN (
        'INVOICE_CREATED', 'INVOICE_SENT', 'PAYMENT_CONFIRMED',
        'REMINDER_SOFT', 'REMINDER_FIRM', 'REMINDER_ESCALATED'
    ))
);

CREATE INDEX idx_notif_tenant_invoice ON notification_logs (tenant_id, invoice_id);
CREATE INDEX idx_notif_type_sent_at   ON notification_logs (type, sent_at);

--rollback DROP TABLE notification_logs;
