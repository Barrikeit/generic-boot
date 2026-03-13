CREATE TABLE user_sessions
(
    id_user_session UUID                     NOT NULL DEFAULT gen_random_uuid(), -- identificador de la relación
    code_user       UUID                     NOT NULL,                           -- identificador del usuario
    jti             CHAR(36)                 NOT NULL,                           -- JWT ID
    jti_pair        CHAR(36)                 NOT NULL,                           -- JWT Pair ID
    issued_at       TIMESTAMP WITH TIME ZONE NOT NULL,                           -- fecha y hora de la creación
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL,                           -- fecha y hora de la expiración
    token_type      VARCHAR(20)              NOT NULL                            -- ACCESS / REFRESH
);

ALTER TABLE user_sessions
    ADD CONSTRAINT pk_user_sessions PRIMARY KEY (id_user_session),
    ADD CONSTRAINT uq_user_jti UNIQUE (code_user, jti),
    ADD CONSTRAINT fk_user_sessions_user FOREIGN KEY (code_user) REFERENCES users (code_user) ON DELETE CASCADE;

CREATE INDEX idx_user_token_user ON user_sessions (code_user);
CREATE INDEX idx_user_token_expires_at ON user_sessions (expires_at);