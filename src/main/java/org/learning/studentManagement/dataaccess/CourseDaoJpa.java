package org.learning.studentManagement.dataaccess;

import jakarta.transaction.Transactional;
import org.learning.studentManagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseDaoJpa extends CourseDao, JpaRepository<Course, Integer> {
    @Modifying
    @Transactional
    @Query("""
            UPDATE Course c
            SET c.name = :#{#course.name},
                c.startDate = :#{#course.startDate},
                c.endDate = :#{#course.endDate}
            WHERE c.id = :#{#course.id}
            """)
    @Override
    void update(@Param("course") Course course); // JpaRepository doesn't generate default update method

}
