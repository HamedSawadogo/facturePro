--liquibase formatted sql

--changeset hamed:002-create-clients
CREATE TABLE clients (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL,
    name            VARCHAR(255) NOT NULL,
    email_value     VARCHAR(255),
    phone_value     VARCHAR(20),
    client_type     VARCHAR(20)  NOT NULL,
    address         TEXT,
    city            VARCHAR(100),
    country         VARCHAR(100),
    tax_number      VARCHAR(50),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,

    -- Audit trail
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_clients PRIMARY KEY (id),
    CONSTRAINT chk_clients_type CHECK (client_type IN ('INDIVIDUAL', 'COMPANY', 'GOVERNMENT'))
);

CREATE INDEX idx_clients_tenant_id ON clients (tenant_id);
CREATE INDEX idx_clients_email ON clients (email_value, tenant_id);
CREATE INDEX idx_clients_name ON clients (LOWER(name), tenant_id);

--rollback DROP TABLE clients;
