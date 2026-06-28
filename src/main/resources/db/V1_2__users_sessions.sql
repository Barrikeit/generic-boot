CREATE TABLE user_sessions
(
    id         UUID                     NOT NULL DEFAULT gen_random_uuid(), -- session identifier
    id_user    UUID                     NOT NULL,                           -- fk -> users.id
    jti        VARCHAR(36)              NOT NULL,                           -- jwt id
    jti_pair   VARCHAR(36)              NOT NULL,                           -- paired jwt id (access <-> refresh)
    issued_at  TIMESTAMP WITH TIME ZONE NOT NULL,                           -- when the token was issued
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,                           -- when the token expires
    token_type VARCHAR(20)              NOT NULL                            -- ACCESS or REFRESH
);

ALTER TABLE user_sessions
    ADD CONSTRAINT pk_user_sessions PRIMARY KEY (id),
    ADD CONSTRAINT uq_jti UNIQUE (jti),
    ADD CONSTRAINT fk_sessions_user FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE;

CREATE INDEX idx_sessions_user ON user_sessions (id_user);
CREATE INDEX idx_sessions_expires_at ON user_sessions (expires_at);