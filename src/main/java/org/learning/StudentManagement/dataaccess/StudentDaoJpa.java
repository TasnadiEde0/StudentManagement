package org.learning.StudentManagement.dataaccess;

import org.learning.StudentManagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDaoJpa extends StudentDao, JpaRepository<Student,Integer> {
}
