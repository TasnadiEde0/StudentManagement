package org.learning.studentManagement.dataaccess;

import org.learning.studentManagement.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Data access object used to interact with the database and obtain Student data
 */

public interface StudentDao {
    Optional<Student> findById(int Id);

    Optional<Student> findByCnp(String cnp);

    Optional<Student> findByEmail(String email);

    List<Student> findAll();

    Student save(Student student);

    void update(Student student);

    void delete(Student student);

}
