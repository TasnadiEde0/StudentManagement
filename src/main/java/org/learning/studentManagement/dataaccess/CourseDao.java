package org.learning.studentManagement.dataaccess;

import org.learning.studentManagement.model.Course;

import java.util.List;
import java.util.Optional;





public interface CourseDao {
    Optional<Course> findById(int Id);

    Optional<Course> findByName(String name);

    List<Course> findAll();

    Course save(Course course);

    void update(Course course);

    void delete(Course course);

}
