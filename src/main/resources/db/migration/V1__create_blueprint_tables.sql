CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    display_name VARCHAR(120) NOT NULL,
    role VARCHAR(32) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE,
    last_updated_at TIMESTAMP(6) WITH TIME ZONE
);

CREATE TABLE user_authentications (
    id UUID PRIMARY KEY,
    provider VARCHAR(32) NOT NULL,
    provider_subject VARCHAR(256) NOT NULL,
    email VARCHAR(320),
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    created_at TIMESTAMP(6) WITH TIME ZONE,
    last_updated_at TIMESTAMP(6) WITH TIME ZONE,
    CONSTRAINT uk_user_authentication_provider_subject UNIQUE (provider, provider_subject)
);

CREATE INDEX idx_user_authentications_account_id ON user_authentications(account_id);

CREATE TABLE stored_files (
    id UUID PRIMARY KEY,
    owner_account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    purpose VARCHAR(32) NOT NULL,
    object_key VARCHAR(512) NOT NULL UNIQUE,
    content_type VARCHAR(128) NOT NULL,
    content_length BIGINT NOT NULL CHECK (content_length > 0),
    uploaded BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE,
    last_updated_at TIMESTAMP(6) WITH TIME ZONE
);

CREATE INDEX idx_stored_files_owner_account_id ON stored_files(owner_account_id);
