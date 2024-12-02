CREATE TABLE users
(
    id_user  BIGINT      NOT NULL,        -- ID column inherited from GenericEntity
    username VARCHAR(50) NOT NULL UNIQUE, -- Non-nullable and unique username
    email    VARCHAR(50),                 -- Email, nullable
    PRIMARY KEY (id_user)                 -- Primary key
);