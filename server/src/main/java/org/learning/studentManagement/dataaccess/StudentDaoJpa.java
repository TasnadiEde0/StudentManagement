package org.learning.studentManagement.dataaccess;

import jakarta.transaction.Transactional;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.data.domain.OffsetScrollPosition;
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

    long count();

    long countByGroup(Group group);

    @Modifying
    @Transactional
    @Query(value = """
                SELECT *
                FROM tb_student s
                WHERE s.group_id = :groupId OR :groupId IS NULL
                ORDER BY CASE WHEN :orderProperty = 'id' THEN s.id END,
                         CASE WHEN :orderProperty = 'firstName' THEN s.first_name END,
                         CASE WHEN :orderProperty = 'lastName' THEN s.last_name END,
                         CASE WHEN :orderProperty = 'email' THEN s.email END
                LIMIT 10
                OFFSET :#{(#page - 1) * 10}
            """,
            nativeQuery = true)
    List<Student> findAllFiltered(
            @Param("groupId") Integer groupId,
            @Param("orderProperty") String orderProperty,
            @Param("page") Integer page
    );

}
