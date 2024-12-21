INSERT INTO users (username, password, email, enabled, banned, ban_date, login_date, login_attempts)
VALUES ('keit', '{bcrypt}$2a$10$iQhdW8kYk9lhAIEBeAe5i.8SJ01ezISpXrI8i1mIZiqkMcprvSJaO', 'keit@keit.com', true, false,
        null, null, 0);

INSERT INTO roles (code_role, role)
VALUES ('AD', 'Admin');

INSERT INTO modules (code_module, module)
VALUES ('ALL', 'Full Access');

INSERT INTO role_modules (id_role, id_module)
VALUES (1, 1);

INSERT INTO user_roles (id_user, id_role)
VALUES (1, 1);