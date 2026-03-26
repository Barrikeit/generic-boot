INSERT INTO users (username, email, password)
VALUES ('keit',
        'keit@keit.com',
        '{bcrypt}$2a$10$iQhdW8kYk9lhAIEBeAe5i.8SJ01ezISpXrI8i1mIZiqkMcprvSJaO');

INSERT INTO user_security (id_user, registration_date, enabled)
VALUES ((SELECT u.id FROM users u WHERE u.username = 'keit'),
        now(),
        true);

INSERT INTO roles (id, code, name)
VALUES (nextval('roles_seq'), 'ADM', 'Admin'),
       (nextval('roles_seq'), 'WTC', 'Watchdog'),
       (nextval('roles_seq'), 'USR', 'User');

INSERT INTO modules (id, code, name)
VALUES (nextval('modules_seq'), 'ALL', 'Full Access'),
       (nextval('modules_seq'), 'EV', 'Event Management'),
       (nextval('modules_seq'), 'USR', 'User Management');

INSERT INTO role_modules (id_role, id_module)
VALUES ((SELECT r.id FROM roles r WHERE r.code = 'ADM'),
        (SELECT m.id FROM modules m WHERE m.code = 'ALL'));

INSERT INTO user_roles (id_user, id_role)
VALUES ((SELECT u.id FROM users u WHERE u.username = 'keit'),
        (SELECT r.id FROM roles r WHERE r.code = 'ADM'));