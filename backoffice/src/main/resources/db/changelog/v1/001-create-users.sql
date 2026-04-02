--liquibase formatted sql

--changeset hamed:001-create-users
CREATE TABLE users (
    id              UUID        NOT NULL DEFAULT gen_random_uuid(),
    tenant_id       UUID        NOT NULL,
    email_value     VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    role            VARCHAR(20)  NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,

    -- Audit trail
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email_tenant UNIQUE (email_value, tenant_id),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'ACCOUNTANT', 'VIEWER'))
);

CREATE INDEX idx_users_tenant_id ON users (tenant_id);
CREATE INDEX idx_users_email ON users (email_value);

--rollback DROP TABLE users;
