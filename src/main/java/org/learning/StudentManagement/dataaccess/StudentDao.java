package org.learning.StudentManagement.dataaccess;

import org.learning.StudentManagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentDao {
    Optional<Student> findById(int Id);

    Student save(Student student);

    Student update(Student student);

    void delete(Student student);

}
