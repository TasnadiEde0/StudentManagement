INSERT INTO tb_group(name) VALUES ('group1'), ('group2'), ('group3');

INSERT INTO tb_course(name, start_date, end_date) VALUES
('course1', '2025-01-01', '2025-12-31'),
('course2', '2025-01-01', '2025-12-31'),
('course3', '2025-01-01', '2025-12-31');

INSERT INTO tb_student(first_name, last_name, cnp, email, group_id, img_name) VALUES
('first_name1', 'last_name1', 'cnp1', 'email1', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('first_name2', 'last_name2', 'cnp2', 'email2', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('first_name3', 'last_name3', 'cnp3', 'email3', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('first_name4', 'last_name4', 'cnp4', 'email4', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('first_name5', 'last_name5', 'cnp5', 'email5', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('first_name6', 'last_name6', 'cnp6', 'email6', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('first_name7', 'last_name7', 'cnp7', 'email7', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('first_name8', 'last_name8', 'cnp8', 'email8', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('first_name9', 'last_name9', 'cnp9', 'email9', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('first_name10', 'last_name10', 'cnp10', 'email10', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('first_name11', 'last_name11', 'cnp11', 'email11', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png'),
('first_name12', 'last_name12', 'cnp12', 'email12', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png'),
('first_name13', 'last_name13', 'cnp13', 'email13', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png'),
('first_name14', 'last_name14', 'cnp14', 'email14', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png'),
('first_name15', 'last_name15', 'cnp15', 'email15', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png');

INSERT INTO tb_student_course(student_id, course_id) VALUES
((SELECT id FROM tb_student WHERE cnp = 'cnp1'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp2'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp3'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp4'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp5'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp6'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp7'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp8'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp9'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp10'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = 'cnp6'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp7'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp8'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp9'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp10'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp11'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp12'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp13'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp14'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp15'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = 'cnp11'), (SELECT id FROM tb_course WHERE name = 'course3')),
((SELECT id FROM tb_student WHERE cnp = 'cnp12'), (SELECT id FROM tb_course WHERE name = 'course3')),
((SELECT id FROM tb_student WHERE cnp = 'cnp13'), (SELECT id FROM tb_course WHERE name = 'course3')),
((SELECT id FROM tb_student WHERE cnp = 'cnp14'), (SELECT id FROM tb_course WHERE name = 'course3')),
((SELECT id FROM tb_student WHERE cnp = 'cnp15'), (SELECT id FROM tb_course WHERE name = 'course3'));
