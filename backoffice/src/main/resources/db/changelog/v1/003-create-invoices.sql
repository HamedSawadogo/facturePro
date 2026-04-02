--liquibase formatted sql

--changeset hamed:003-create-invoices
CREATE TABLE invoices (
    id                      UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id               UUID         NOT NULL,
    invoice_number          VARCHAR(30)  UNIQUE,
    type                    VARCHAR(20)  NOT NULL,
    status                  VARCHAR(20)  NOT NULL,
    client_id               UUID         NOT NULL,
    client_name             VARCHAR(255) NOT NULL,
    issue_date              DATE         NOT NULL,
    due_date                DATE         NOT NULL,

    -- Money: total HT
    total_ht_amount         DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_ht_currency       VARCHAR(3)   NOT NULL DEFAULT 'XOF',

    -- Money: total TTC
    total_ttc_amount        DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_ttc_currency      VARCHAR(3)   NOT NULL DEFAULT 'XOF',

    -- Money: montant payé
    amount_paid_amount      DECIMAL(15,2) NOT NULL DEFAULT 0,
    amount_paid_currency    VARCHAR(3)   NOT NULL DEFAULT 'XOF',

    notes                   TEXT,
    payment_terms           VARCHAR(255),
    idempotency_key         VARCHAR(255) UNIQUE,

    -- Audit trail
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    version                 BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_invoices PRIMARY KEY (id),
    CONSTRAINT chk_invoices_type   CHECK (type   IN ('INVOICE', 'QUOTE', 'CREDIT_NOTE')),
    CONSTRAINT chk_invoices_status CHECK (status IN ('DRAFT', 'SENT', 'PARTIALLY_PAID', 'PAID', 'OVERDUE', 'CANCELLED')),
    CONSTRAINT fk_invoices_client  FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE INDEX idx_invoices_tenant_id  ON invoices (tenant_id);
CREATE INDEX idx_invoices_client_id  ON invoices (client_id);
CREATE INDEX idx_invoices_status     ON invoices (status);
CREATE INDEX idx_invoices_due_date   ON invoices (due_date);
CREATE INDEX idx_invoices_tenant_status ON invoices (tenant_id, status);

--changeset hamed:003-create-invoice-items
CREATE TABLE invoice_items (
    id                      UUID         NOT NULL DEFAULT gen_random_uuid(),
    invoice_id              UUID         NOT NULL,
    description             TEXT         NOT NULL,
    quantity                DECIMAL(10,2) NOT NULL,
    unit_price_amount       DECIMAL(15,2) NOT NULL,
    unit_price_currency     VARCHAR(3)   NOT NULL DEFAULT 'XOF',
    tax_rate                DECIMAL(5,2) NOT NULL DEFAULT 0,

    CONSTRAINT pk_invoice_items    PRIMARY KEY (id),
    CONSTRAINT fk_items_invoice    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);

CREATE INDEX idx_invoice_items_invoice_id ON invoice_items (invoice_id);

--rollback DROP TABLE invoice_items; DROP TABLE invoices;
