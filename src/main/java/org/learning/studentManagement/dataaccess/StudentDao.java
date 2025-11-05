package org.learning.studentManagement.dataaccess;

import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.data.domain.OffsetScrollPosition;

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

    long count();

    long countByGroup(Group group);

    List<Student> findTop10ByOrderByFirstNameAsc(OffsetScrollPosition offset);

    List<Student> findTop10ByOrderByLastNameAsc(OffsetScrollPosition offset);

    List<Student> findTop10ByOrderByEmailAsc(OffsetScrollPosition offset);

    List<Student> findTop10ByOrderByIdAsc(OffsetScrollPosition offset);

    List<Student> findTop10ByGroupOrderByFirstNameAsc(Group group, OffsetScrollPosition offset);

    List<Student> findTop10ByGroupOrderByLastNameAsc(Group group, OffsetScrollPosition offset);

    List<Student> findTop10ByGroupOrderByEmailAsc(Group group, OffsetScrollPosition offset);

    List<Student> findTop10ByGroupOrderByIdAsc(Group group, OffsetScrollPosition offset);
}
