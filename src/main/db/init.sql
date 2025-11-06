CREATE DATABASE SpringServer;

\c springserver

create table users(
	username varchar(50) not null primary key,
	password varchar(500) not null,
	enabled boolean not null
);

create table authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

INSERT INTO users(username, password, enabled) VALUES
('user', '$2a$10$eS7yqwY7a.W0ekmpiJmQj.V6fIOeRYk7iRYaMdVbg31xTW1EXuLIO', 't'),
('admin', '$2a$10$4EQ7bMfL4SXtBbnH0iICRuzlQmehvKENVdJUS8ct.l2TPO21Yl1p2', 't');

INSERT INTO authorities(authority, username) VALUES
('ROLE_USER', 'user'),
('ROLE_ADMIN', 'admin'),
('ROLE_USER', 'admin');

create table persistent_logins (
    username varchar(64) not null,
    series varchar(64) primary key,
    token varchar(64) not null,
    last_used timestamp not null
);
