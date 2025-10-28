package org.learning.studentManagement.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.learning.studentManagement.dataaccess.CourseDao;
import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Find the Course with the provided {@code id}
     *
     * @param id Id of the queried Course
     * @return Course with the given {@code id}
     * @throws IllegalArgumentException If the {@code id} belongs to no Course
     */
    @Override
    public Course findById(int id) throws IllegalArgumentException {
        return courseDao.findById(id).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a course!"));

    }

    /**
     * Find the Course with the provided {@code name}
     *
     * @param name Name of the queried Course
     * @return If exists Course with the given {@code name}
     */
    @Override
    public Optional<Course> findByName(String name) {
        return courseDao.findByName(name);
    }

    /**
     * Returns all Courses
     *
     * @return Every Course
     */
    @Override
    public List<Course> findAll() {
        return courseDao.findAll();
    }

    /**
     * Create and save a Course with the given properties
     *
     * @param name      New Course name
     * @param startDate Staring date of the Course
     * @param endDate   Ending date of the Course
     * @return Saved Course
     * @throws IllegalArgumentException If the {@code startDate} is after the {@code endDate}
     */
    @Override
    public Course save(String name, LocalDate startDate, LocalDate endDate) throws IllegalArgumentException {
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
     * Update the Course with the provided values
     *
     * @param id        Id of the Course to be modified. Must belong to a Course
     * @param name      New name of the Course or {@code null}
     * @param startDate New staring date or {@code null}
     * @param endDate   New ending date or {@code null}
     * @throws IllegalArgumentException If the id belongs to no Course or the {@code startDate} is after the {@code endDate}
     */
    @Override
    public void update(Integer id, String name, LocalDate startDate, LocalDate endDate) throws IllegalArgumentException {
        Course course = findById(id);

        if (name != null && !name.isEmpty()) {
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

    /**
     * Delete the Course associated with the {@code id}
     *
     * @param id Id of the Course to be deleted
     * @throws IllegalArgumentException If the {@code id} doesn't belong to a Course
     */
    @Override
    public void delete(Integer id) throws IllegalArgumentException {
        Course course = findById(id);

        courseDao.delete(course);

    }

    /**
     * Add a Student to a Course
     *
     * @param studentId Id of the Student to be added to a Course
     * @param courseId  Id of the Course
     * @throws IllegalArgumentException If the {@code studentId} is not associated with a Student,
     *                                  the {@code courseId} is not associated with a Course or if the two are already associated
     */
    @Override
    @Transactional
    public void addStudent(Integer studentId, Integer courseId) throws IllegalArgumentException {
        Course course = findById(courseId);

        Student student = studentDao.findById(studentId).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a student!"));

        if (!course.getStudents().contains(student) || student.getCourses().contains(course)) {
            course.getStudents().add(student);
            student.getCourses().add(course);

            entityManager.persist(course);
            entityManager.persist(student);

        } else {
            throw new IllegalArgumentException("The given student is part of the course!");
        }

    }

    /**
     * Remove a student from a Course
     *
     * @param studentId Id of the Student to be removed from a Course
     * @param courseId  Id of the Course
     * @throws IllegalArgumentException If the {@code studentId} is not associated with a Student,
     *                                  the {@code courseId} is not associated with a Course or if the two are not associated
     */
    @Override
    @Transactional
    public void removeStudent(Integer studentId, Integer courseId) throws IllegalArgumentException {
        Course course = findById(courseId);

        Student student = studentDao.findById(studentId).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a student!"));

        if (course.getStudents().contains(student) && student.getCourses().contains(course)) {
            course.getStudents().remove(student);
            student.getCourses().remove(course);

            entityManager.persist(course);
            entityManager.persist(student);

        } else {
            throw new IllegalArgumentException("The given student isn't part of the course!");
        }

    }

}
