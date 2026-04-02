--liquibase formatted sql

--changeset hamed:004-create-payments
CREATE TABLE payments (
    id                  UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id           UUID         NOT NULL,
    invoice_id          UUID         NOT NULL,
    amount_value        DECIMAL(15,2) NOT NULL,
    amount_currency     VARCHAR(3)   NOT NULL DEFAULT 'XOF',
    method              VARCHAR(20)  NOT NULL,
    status              VARCHAR(20)  NOT NULL,
    payment_date        DATE         NOT NULL,
    reference           VARCHAR(255),
    idempotency_key     VARCHAR(255) NOT NULL UNIQUE,
    notes               TEXT,

    -- Audit trail
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT chk_payments_method CHECK (method IN (
        'ORANGE_MONEY', 'MTN_MOMO', 'MOOV_MONEY',
        'BANK_TRANSFER', 'CASH', 'CHEQUE', 'CARD'
    )),
    CONSTRAINT chk_payments_status CHECK (status IN (
        'PENDING', 'CONFIRMED', 'FAILED', 'REFUNDED'
    )),
    CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

CREATE INDEX idx_payments_invoice_id     ON payments (invoice_id);
CREATE INDEX idx_payments_tenant_id      ON payments (tenant_id);
CREATE INDEX idx_payments_status         ON payments (status);
CREATE INDEX idx_payments_idempotency    ON payments (idempotency_key);

--rollback DROP TABLE payments;
