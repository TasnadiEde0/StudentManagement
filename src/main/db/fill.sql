INSERT INTO tb_group(name) VALUES ('group1'), ('group2'), ('group3');

INSERT INTO tb_course(name, start_date, end_date) VALUES
('course1', '2025-01-01', '2025-12-31'),
('course2', '2025-01-01', '2025-12-31'),
('course3', '2025-01-01', '2025-12-31');

INSERT INTO tb_student(first_name, last_name, cnp, email, group_id, img_name) VALUES
('firstname1', 'lastname1', '0000000000001', 'email1', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('firstname2', 'lastname2', '0000000000002', 'email2', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('firstname3', 'lastname3', '0000000000003', 'email3', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('firstname4', 'lastname4', '0000000000004', 'email4', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('firstname5', 'lastname5', '0000000000005', 'email5', (SELECT id FROM tb_group WHERE name = 'group1'), 'pic.png'),
('firstname6', 'lastname6', '0000000000006', 'email6', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('firstname7', 'lastname7', '0000000000007', 'email7', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('firstname8', 'lastname8', '0000000000008', 'email8', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('firstname9', 'lastname9', '0000000000009', 'email9', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('firstname10', 'lastname10', '0000000000010', 'email10', (SELECT id FROM tb_group WHERE name = 'group2'), 'pic.png'),
('firstname11', 'lastname11', '0000000000011', 'email11', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png'),
('firstname12', 'lastname12', '0000000000012', 'email12', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png'),
('firstname13', 'lastname13', '0000000000013', 'email13', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png'),
('firstname14', 'lastname14', '0000000000014', 'email14', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png'),
('firstname15', 'lastname15', '0000000000015', 'email15', (SELECT id FROM tb_group WHERE name = 'group3'), 'pic.png');

INSERT INTO tb_student_course(student_id, course_id) VALUES
((SELECT id FROM tb_student WHERE cnp = '0000000000001'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000002'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000003'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000004'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000005'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000006'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000007'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000008'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000009'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000010'), (SELECT id FROM tb_course WHERE name = 'course1')),
((SELECT id FROM tb_student WHERE cnp = '0000000000006'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000007'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000008'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000009'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000010'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000011'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000012'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000013'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000014'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000015'), (SELECT id FROM tb_course WHERE name = 'course2')),
((SELECT id FROM tb_student WHERE cnp = '0000000000011'), (SELECT id FROM tb_course WHERE name = 'course3')),
((SELECT id FROM tb_student WHERE cnp = '0000000000012'), (SELECT id FROM tb_course WHERE name = 'course3')),
((SELECT id FROM tb_student WHERE cnp = '0000000000013'), (SELECT id FROM tb_course WHERE name = 'course3')),
((SELECT id FROM tb_student WHERE cnp = '0000000000014'), (SELECT id FROM tb_course WHERE name = 'course3')),
((SELECT id FROM tb_student WHERE cnp = '0000000000015'), (SELECT id FROM tb_course WHERE name = 'course3'));
