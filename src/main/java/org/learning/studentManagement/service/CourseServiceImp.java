package org.learning.studentManagement.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.dataaccess.CourseDao;
import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.model.dto.CourseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CourseServiceImp implements CourseService {
    @Autowired
    private CourseDao courseDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private EntityManager entityManager;

    private void courseDuplicateCheck(String name) throws IllegalArgumentException {
        Optional<Course> testCourse = courseDao.findByName(name);
        if (testCourse.isPresent()) {
            throw new IllegalArgumentException("Course name already taken!");
        }
    }

    /**
     * Find the Course with the provided {@code id}
     *
     * @param id Id of the queried Course
     * @return Course with the given {@code id}
     * @throws IllegalArgumentException If the {@code id} belongs to no Course
     */
    @Override
    public Course findById(int id) throws IllegalArgumentException {
        Course course = courseDao.findById(id).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a course!"));
        log.info("Course with ID {} retrieved: {}", course.getId(), course);

        return course;

    }

    /**
     * Find the Course with the provided {@code name}
     *
     * @param name Name of the queried Course
     * @return If exists Course with the given {@code name}
     */
    @Override
    public Optional<Course> findByName(String name) {
        Optional<Course> course = courseDao.findByName(name);
        course.ifPresent(value -> log.info("Course with name {} retrieved: {}", name, value));

        return course;
    }

    /**
     * Returns all Courses
     *
     * @return Every Course
     */
    @Override
    public List<Course> findAll() {
        List<Course> courses = courseDao.findAll();
        log.info("Courses retrieved: {}", courses);

        return courses;
    }

    /**
     * Create and save a Course with the given properties
     *
     * @param courseDto The data of a student
     * @return Saved Course
     * @throws IllegalArgumentException If the {@code startDate} is
     *                                  after the {@code endDate} or {@code name} isn't unique
     */
    @Override
    @Transactional
    public Course save(
            CourseDto courseDto
    ) throws IllegalArgumentException {
        Course course = new Course();

        courseDuplicateCheck(courseDto.getName());
        course.setName(courseDto.getName());

        if (courseDto.getStartDate() == null) {
            throw new IllegalArgumentException("Starting Date can't be null");
        }
        if (courseDto.getEndDate() == null) {
            throw new IllegalArgumentException("Ending Date can't be null");
        }

        if (courseDto.getStartDate().isAfter(courseDto.getEndDate())) {
            throw new IllegalArgumentException("The given start date is after the given end date!");
        }

        course.setStartDate(courseDto.getStartDate());
        course.setEndDate(courseDto.getEndDate());

        log.info("Course saved: {}", course);

        return courseDao.save(course);
    }

    /**
     * Update the Course with the provided values
     *
     * @param courseDto The data of a student
     * @throws IllegalArgumentException If the id belongs to no Course or the {@code startDate} is after the {@code endDate}
     */
    @Override
    @Transactional
    public void update(CourseDto courseDto) throws IllegalArgumentException {
        Course course = findById(Integer.parseInt(courseDto.getId()));

        if (courseDto.getName() != null && !courseDto.getName().isEmpty()) {
            course.setName(courseDto.getName());
        }
        if (courseDto.getStartDate() != null) {
            course.setStartDate(courseDto.getStartDate());
        }
        if (courseDto.getEndDate() != null) {
            course.setEndDate(courseDto.getEndDate());
        }

        if (course.getStartDate().isAfter(course.getEndDate())) {
            throw new IllegalArgumentException("The given start date is after the given end date!");
        }

        log.info("Course updated: {}", course);

        courseDao.update(course);

    }

    /**
     * Delete the Course associated with the {@code id}
     *
     * @param id Id of the Course to be deleted
     * @throws IllegalArgumentException If the {@code id} doesn't belong to a Course
     */
    @Override
    @Transactional
    public void delete(Integer id) throws IllegalArgumentException {
        Course course = findById(id);

        //detach course from students
        for (Student student : course.getStudents()) {
            student.getCourses().remove(course);
            entityManager.persist(student);
        }
        course.setStudents(new ArrayList<>());
        entityManager.persist(course);

        courseDao.delete(course);

        log.info("Course deleted: {}", course);

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

        if (!course.getStudents().contains(student) || !student.getCourses().contains(course)) {
            course.getStudents().add(student);
            student.getCourses().add(course);

            entityManager.persist(course);
            entityManager.persist(student);

            log.info("{} added to {}", student, course);

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

            log.info("{} removed from {}", student, course);

        } else {
            throw new IllegalArgumentException("The given student isn't part of the course!");
        }

    }

}
