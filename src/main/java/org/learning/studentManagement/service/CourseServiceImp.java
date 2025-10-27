package org.learning.studentManagement.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.learning.studentManagement.dataaccess.CourseDao;
import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImp implements CourseService {
    @Autowired
    private CourseDao courseDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Course findById(int id) {
        return courseDao.findById(id).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a course!"));
    }

    @Override
    public Optional<Course> findByName(String name) {
        return courseDao.findByName(name);
    }

    @Override
    public List<Course> findAll() {
        return courseDao.findAll();
    }

    @Override
    public Course save(String name, LocalDate startDate, LocalDate endDate) {
        Course course = new Course();
        course.setName(name);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("The given start date is after the given end date!");
        }

        course.setStartDate(startDate);
        course.setEndDate(endDate);

        return courseDao.save(course);
    }

    /**
     *
     * @param id
     * @param name
     * @param startDate
     * @param endDate
     *
     * DOES NOT UPDATE ENROLLED STUDENT LIST
     */
    @Override
    public void update(Integer id, String name, LocalDate startDate, LocalDate endDate) {
        Course course = findById(id);

        if (!name.isEmpty()) {
            course.setName(name);
        }
        if (startDate != null) {
            course.setStartDate(startDate);
        }
        if (endDate != null) {
            course.setEndDate(endDate);
        }

        if (course.getStartDate().isAfter(course.getEndDate())) {
            throw new IllegalArgumentException("The given start date is after the given end date!");
        }

        courseDao.update(course);

    }

    @Override
    public void delete(Integer id) {
        Course course = findById(id);

        courseDao.delete(course);

    }

    @Override
    @Transactional
    public void addStudent(Integer studentId, Integer courseId) {
        Course course = findById(courseId);

        Student student = studentDao.findById(studentId).orElseThrow(() -> new IllegalArgumentException("The given ID isn't associated with a student!"));

        if (!course.getStudents().contains(student) || student.getCourses().contains(course)) {
            course.getStudents().add(student);
            student.getCourses().add(course);

            entityManager.persist(course);
            entityManager.persist(student);

        }
        else {
            throw new IllegalArgumentException("The given student is part of the course!");
        }

    }

    @Override
    @Transactional
    public void removeStudent(Integer studentId, Integer courseId) {
        Course course = findById(courseId);

        Student student = studentDao.findById(studentId).orElseThrow(() -> new IllegalArgumentException("The given ID isn't associated with a student!"));

        if (course.getStudents().contains(student) && student.getCourses().contains(course)) {
            course.getStudents().remove(student);
            student.getCourses().remove(course);

            entityManager.persist(course);

        }
        else {
            throw new IllegalArgumentException("The given student isn't part of the course!");
        }

    }

}
