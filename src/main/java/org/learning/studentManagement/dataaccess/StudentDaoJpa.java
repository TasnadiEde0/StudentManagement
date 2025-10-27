package org.learning.studentManagement.dataaccess;

import jakarta.transaction.Transactional;
import org.learning.studentManagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentDaoJpa extends StudentDao, JpaRepository<Student, Integer> {

    @Modifying
    @Transactional
    @Query(value = """ 
            UPDATE Student s
            SET s.firstName = :#{#student.firstName},
                s.lastName = :#{#student.lastName},
                s.cnp = :#{#student.cnp},
                s.email = :#{#student.email},
                s.group.id = :#{#student.group.id}
            WHERE s.id = :#{#student.id}
            """)
    @Override
    void update(@Param("student") Student student); // JpaRepository doesn't generate default update method


}
