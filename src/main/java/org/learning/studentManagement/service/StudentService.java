package org.learning.studentManagement.service;

import org.learning.studentManagement.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Processing for Student data
 */
public interface StudentService {
    Optional<Student> findById(int Id);

    Optional<Student> findByCnp(String cnp);

    Optional<Student> findByEmail(String email);

    List<Student> findAll();

    Student save(Student student);

    void update(Student student);

    void delete(Student student);

}
