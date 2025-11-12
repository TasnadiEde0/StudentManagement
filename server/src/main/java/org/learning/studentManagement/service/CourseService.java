package org.learning.studentManagement.service;

import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.dto.CourseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    Course findById(int Id);

    Optional<Course> findByName(String name);

    List<Course> findAll();

    Course save(CourseDto courseDto);

    void update(CourseDto courseDto);

    void delete(Integer id);

    void removeStudent(Integer studentId, Integer courseId);

    void addStudent(Integer studentId, Integer courseId);

}
