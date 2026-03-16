INSERT INTO users (username, email, password)
VALUES ('keit', 'keit@keit.com',
        '{bcrypt}$2a$10$iQhdW8kYk9lhAIEBeAe5i.8SJ01ezISpXrI8i1mIZiqkMcprvSJaO');

INSERT INTO user_security (id_user, registration_date, enabled)
VALUES ((SELECT u.id FROM users u WHERE u.username = 'keit'), now(), true);

INSERT INTO roles (code, name)
VALUES ('ADM', 'Admin'),
       ('WTC', 'Watchdog'),
       ('US', 'User');

INSERT INTO modules (code, name)
VALUES ('ALL', 'Full Access'),
       ('EV', 'Event Management'),
       ('US', 'User Management');

INSERT INTO role_modules (id_role, id_module)
VALUES ((SELECT r.id FROM roles r WHERE r.code_role = 'AD'), (SELECT m.id FROM modules m WHERE m.code_module = 'ALL'));

INSERT INTO user_roles (id_user, id_role)
VALUES ((SELECT u.id FROM users u WHERE u.username = 'keit'), (SELECT r.id FROM roles r WHERE r.code_role = 'AD'));