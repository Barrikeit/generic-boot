INSERT INTO users (code_user, username, email, password, enabled)
VALUES ('5cc35df7-18b6-462a-8140-22b0db76c553', 'keit', 'keit@keit.com',
        '{bcrypt}$2a$10$iQhdW8kYk9lhAIEBeAe5i.8SJ01ezISpXrI8i1mIZiqkMcprvSJaO', true);

INSERT INTO roles (code_role, role)
VALUES ('AD', 'Admin'),
       ('US', 'User');

INSERT INTO modules (code_module, module)
VALUES ('ALL', 'Full Access'),
       ('EV', 'Event Management'),
       ('US', 'User Management');

INSERT INTO role_modules (id_role, id_module)
VALUES (1, 1);

INSERT INTO user_roles (id_user, id_role)
VALUES (1, 1);