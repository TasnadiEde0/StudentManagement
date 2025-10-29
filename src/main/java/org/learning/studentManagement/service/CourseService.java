package org.learning.studentManagement.service;

import org.learning.studentManagement.model.Course;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    Course findById(int Id);

    Optional<Course> findByName(String name);

    List<Course> findAll();

    Course save(String name, LocalDate startDate, LocalDate endDate);

    void update(Integer id, String name, LocalDate startDate, LocalDate endDate);

    void delete(Integer id);

    void removeStudent(Integer studentId, Integer courseId);

    void addStudent(Integer studentId, Integer courseId);

}
