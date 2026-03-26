CREATE TABLE locations
(
    id      BIGINT       NOT NULL, -- unique location identifier
    code    VARCHAR(10)  NOT NULL, -- short abbreviation e.g. 'MAD', 'BCN'
    country VARCHAR(255) NOT NULL, -- country name
    city    VARCHAR(255) NULL      -- city name
);

CREATE TABLE directions
(
    id          UUID         NOT NULL DEFAULT gen_random_uuid(), -- unique direction identifier
    id_location BIGINT       NOT NULL,                           -- fk -> locations.id
    street      VARCHAR(255) NOT NULL,                           -- street name
    number      VARCHAR(20)  NOT NULL,                           -- street number
    postal_code VARCHAR(20)  NULL,                               -- postal / zip code
    extra       VARCHAR(255) NULL                                -- floor, door, apt, etc.
);

CREATE TABLE modules
(
    id   BIGINT       NOT NULL, -- unique module identifier
    code VARCHAR(3)   NOT NULL, -- short abbreviation e.g. 'SEC', 'EV'
    name VARCHAR(200) NOT NULL  -- module display name
);

CREATE TABLE roles
(
    id   BIGINT      NOT NULL, -- unique role identifier
    code VARCHAR(3)  NOT NULL, -- short abbreviation e.g. 'ADM', 'AUD', 'USR'
    name VARCHAR(50) NOT NULL  -- role display name
);

CREATE TABLE users
(
    id           UUID         NOT NULL DEFAULT gen_random_uuid(), -- unique user identifier
    username     VARCHAR(50)  NOT NULL,                           -- login username
    name         VARCHAR(50)  NULL,                               -- first name
    surname1     VARCHAR(50)  NULL,                               -- first surname
    surname2     VARCHAR(50)  NULL,                               -- second surname
    email        VARCHAR(100) NOT NULL,                           -- contact and login email
    phone        VARCHAR(50)  NULL,                               -- optional phone for 2fa
    password     VARCHAR(255) NOT NULL,                           -- hashed password
    id_direction UUID         NULL                                -- fk -> directions.id (nullable)
);

CREATE TABLE user_security
(
    id_user            UUID                     NOT NULL,               -- pk + fk -> users.id
    registration_date  TIMESTAMP WITH TIME ZONE NULL,                   -- when the account was created
    verification_token VARCHAR(20)              NULL,                   -- token to activate the account
    enabled            BOOLEAN                  NOT NULL DEFAULT false, -- whether the account is active
    login_attempts     INTEGER                  NOT NULL DEFAULT 0,     -- failed login counter
    login_date         TIMESTAMP WITH TIME ZONE NULL,                   -- last successful login
    banned             BOOLEAN                  NOT NULL DEFAULT false, -- whether the account is banned
    ban_date           TIMESTAMP WITH TIME ZONE NULL,                   -- when the ban was applied
    ban_reason         VARCHAR(255)             NULL                    -- reason for the ban
);

CREATE TABLE role_modules
(
    id_module BIGINT NOT NULL, -- fk -> modules.id
    id_role   BIGINT NOT NULL  -- fk -> roles.id
);

CREATE TABLE user_roles
(
    id_role BIGINT NOT NULL, -- fk -> roles.id
    id_user UUID   NOT NULL  -- fk -> users.id
);

ALTER TABLE locations
    ADD CONSTRAINT pk_locations PRIMARY KEY (id),
    ADD CONSTRAINT uq_locations UNIQUE (code);

ALTER TABLE directions
    ADD CONSTRAINT pk_directions PRIMARY KEY (id),
    ADD CONSTRAINT fk_directions_location FOREIGN KEY (id_location) REFERENCES locations (id);


ALTER TABLE modules
    ADD CONSTRAINT pk_modules PRIMARY KEY (id),
    ADD CONSTRAINT uq_modules UNIQUE (code);

ALTER TABLE roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id),
    ADD CONSTRAINT uq_roles UNIQUE (code);

ALTER TABLE users
    ADD CONSTRAINT pk_users PRIMARY KEY (id),
    ADD CONSTRAINT uq_users_username UNIQUE (username),
    ADD CONSTRAINT uq_users_email UNIQUE (email),
    ADD CONSTRAINT fk_users_direction FOREIGN KEY (id_direction) REFERENCES directions (id);

ALTER TABLE user_security
    ADD CONSTRAINT pk_user_security PRIMARY KEY (id_user),
    ADD CONSTRAINT fk_security_user FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE role_modules
    ADD CONSTRAINT pk_role_modules PRIMARY KEY (id_module, id_role),
    ADD CONSTRAINT fk_rolmod_module FOREIGN KEY (id_module) REFERENCES modules (id),
    ADD CONSTRAINT fk_rolmod_role FOREIGN KEY (id_role) REFERENCES roles (id);

ALTER TABLE user_roles
    ADD CONSTRAINT pk_user_roles PRIMARY KEY (id_role, id_user),
    ADD CONSTRAINT fk_userol_role FOREIGN KEY (id_role) REFERENCES roles (id),
    ADD CONSTRAINT fk_userol_user FOREIGN KEY (id_user) REFERENCES users (id);

CREATE SEQUENCE IF NOT EXISTS locations_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS modules_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS roles_seq START WITH 1 INCREMENT BY 1;