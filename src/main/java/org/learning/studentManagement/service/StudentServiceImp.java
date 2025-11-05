package org.learning.studentManagement.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.dataaccess.CourseDao;
import org.learning.studentManagement.dataaccess.GroupDao;
import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StudentServiceImp implements StudentService {
    @Autowired
    private StudentDao studentDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private EntityManager entityManager;

    private final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/imgs";

    /**
     * Check if the email already belongs to a Student
     *
     * @param email Email to be checked
     * @throws IllegalArgumentException If the email is taken
     */
    private void emailDuplicateCheck(String email) throws IllegalArgumentException {
        Optional<Student> testEmail = studentDao.findByEmail(email);
        if (testEmail.isPresent()) {
            throw new IllegalArgumentException("Student Email already taken!");
        }
    }

    /**
     * Check if the CNP already belongs to a Student
     *
     * @param cnp CNP to be checked
     * @throws IllegalArgumentException If the CNP is taken
     */
    private void cnpDuplicateCheck(String cnp) throws IllegalArgumentException {
        Optional<Student> testCnp = studentDao.findByCnp(cnp);
        if (testCnp.isPresent()) {
            throw new IllegalArgumentException("Student CNP already taken!");
        }
    }

    /**
     * If the given name already belongs to a Group, that Group is selected,
     * otherwise a new Group is created with the given name.
     *
     * @param name A group name
     * @return A group that is associated with the given name
     */
    private Group fetchGroup(String name) {
        Group group = groupDao.findByName(name).orElse(null);

        if (group == null) {
            group = new Group();
            group.setName(name);
        }

        group = groupDao.save(group);

        return group;

    }

    /**
     * Find the Student with the provided {@code id}
     *
     * @param Id Id of the queried Student
     * @return Student with the given {@code id}
     * @throws IllegalArgumentException If the {@code id} doesn't belong to a Student
     */
    @Override
    public Student findById(int Id) throws IllegalArgumentException {
        Student student = studentDao.findById(Id).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a student!"));
        log.info("Student with ID {} retrieved: {}", student.getId(), student);

        return student;

    }

    /**
     * Find the Student with the provided {@code cnp}
     *
     * @param cnp CNP of the queried Student
     * @return If exists Student with the given {@code cnp}
     */
    @Override
    public Optional<Student> findByCnp(String cnp) {
        Optional<Student> student = studentDao.findByCnp(cnp);
        student.ifPresent(value -> log.info("Student with CNP {} retrieved: {}", value.getCnp(), value));

        return student;
    }

    /**
     * Find the Student with the provided {@code email}
     *
     * @param email Email of the queried Student
     * @return If exists Student with the given {@code email}
     */
    @Override
    public Optional<Student> findByEmail(String email) {
        Optional<Student> student = studentDao.findByEmail(email);
        student.ifPresent(value -> log.info("Student with Email {} retrieved: {}", value.getEmail(), value));

        return student;
    }

    /**
     * Returns all Student
     *
     * @return Every Student
     */
    @Override
    public List<Student> findAll() {
        List<Student> students = studentDao.findAll();
        log.info("Students retrieved: {}", students);

        return students;
    }

    public int count() {
        return Math.toIntExact(studentDao.count());
    }

    public int countByGroup(Group group) {
        return Math.toIntExact(studentDao.countByGroup(group));
    }

    private OffsetScrollPosition scrollPosition(int page) {
        if(page == 1) {
            return ScrollPosition.offset();
        }
        return ScrollPosition.offset((page - 1) * 10L - 1);
    }

    public List<Student> findAllFiltered(Group group, String orderProperty, int page) {
        List<Student> students;
        if (group == null) {
            switch (orderProperty) {
                case "firstName" -> students = studentDao.findTop10ByOrderByFirstNameAsc(scrollPosition(page));
                case "lastName" -> students = studentDao.findTop10ByOrderByLastNameAsc(scrollPosition(page));
                case "email" -> students = studentDao.findTop10ByOrderByEmailAsc(scrollPosition(page));
                default -> students = studentDao.findTop10ByOrderByIdAsc(scrollPosition(page));
            }
        }
        else {
            switch (orderProperty) {
                case "firstName" -> students = studentDao.findTop10ByGroupOrderByFirstNameAsc(group, scrollPosition(page));
                case "lastName" -> students = studentDao.findTop10ByGroupOrderByLastNameAsc(group, scrollPosition(page));
                case "email" -> students = studentDao.findTop10ByGroupOrderByEmailAsc(group, scrollPosition(page));
                default -> students = studentDao.findTop10ByGroupOrderByIdAsc(group, scrollPosition(page));
            }
        }


        return students;
    }

    /**
     * Create and save a Student with the given properties
     *
     * @param firstName First name of the new Student
     * @param lastName  Last name of the new Student
     * @param email     Email of the new Student
     * @param cnp       CNP of the new Student
     * @param groupName Name of the Group the new Student will belong to
     * @param file      Profile picture of the new Student
     * @return Saved Student
     * @throws IllegalArgumentException If no picture has been uploaded or if the {@code email} or {@code cnp} is not unique
     * @throws IOException              In case of access errors for the {@code file}
     */
    @Override
    @Transactional
    public Student save(
            String firstName,
            String lastName,
            String email,
            String cnp,
            String groupName,
            MultipartFile file
    ) throws IllegalArgumentException, IOException {

        Student student = new Student();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("No picture has been uploaded!");
        }

        Path imgPath = Paths.get(UPLOAD_DIRECTORY, cnp + ".png");
        Files.write(imgPath, file.getBytes());
        student.setImgName(cnp + ".png");

        student.setFirstName(firstName);
        student.setLastName(lastName);

        emailDuplicateCheck(email);
        student.setEmail(email);

        cnpDuplicateCheck(cnp);
        student.setCnp(cnp);

        studentDao.save(student);

        Group group = fetchGroup(groupName);

        student.setGroup(group);

        groupDao.save(group);
        studentDao.update(student);

        log.info("Student saved: {}", student);

        return student;

    }

    /**
     * Update the Student with the provided values
     *
     * @param id        Id of the Student to be modified. Must belong to a Student
     * @param firstName New first name of the Student or null
     * @param lastName  New last name of the Student or null
     * @param email     New email of the Student or null
     * @param cnp       New CNP of the Student or null
     * @param groupid   Id of the new Group the Student will belong to or null
     * @param file      New profile picture of the Student or null
     * @throws IllegalArgumentException If the {@code email} or {@code cnp} is not unique
     * @throws IOException              In case of access errors for the {@code file}
     */
    @Override
    @Transactional
    public void update(
            String id,
            String firstName,
            String lastName,
            String email,
            String cnp,
            String groupid,
            MultipartFile file
    ) throws IOException, IllegalArgumentException {
        Student student = findById(Integer.parseInt(id));

        if (firstName != null && !firstName.isEmpty()) {
            student.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            student.setLastName(lastName);
        }
        if (email != null && !email.isEmpty()) {
            emailDuplicateCheck(email);
            student.setEmail(email);
        }
        if (cnp != null && !cnp.isEmpty()) {
            cnpDuplicateCheck(cnp);
            student.setCnp(cnp);
        }
        if (groupid != null && !groupid.isEmpty()) {
            student.setGroup(groupDao.findById(Integer.parseInt(groupid)).orElseThrow(() ->
                    new IllegalArgumentException("The given ID isn't associated with a group!")));
        }

        if (!file.isEmpty()) {
            Path imgPath = Paths.get(UPLOAD_DIRECTORY, student.getCnp() + ".png");
            Files.write(imgPath, file.getBytes());
            student.setImgName(student.getCnp() + ".png");
        }

        studentDao.update(student);

        log.info("Student updated: {}", student);

    }

    /**
     * Delete the Student associated with the {@code id}
     *
     * @param id Id of the Student to be deleted
     * @throws IllegalArgumentException If the {@code id} doesn't belong to a Course
     */
    @Override
    @Transactional
    public void delete(String id) throws IllegalArgumentException {
        Student student = findById(Integer.parseInt(id));

        //detach courses from student
        for (Course course : student.getCourses()) {
            course.getStudents().remove(student);
            entityManager.persist(course);
        }
        student.setCourses(new ArrayList<>());
        entityManager.persist(student);

        studentDao.delete(student);

        log.info("Student deleted: {}", student);

    }

    /**
     * Selects a Student profile picture and returns it as a {@code Resource}
     *
     * @param imgName Name of the profile picture
     * @return Profile picture as a {@code Resource}
     * @throws FileNotFoundException If the image doesn't exist
     */
    @Override
    public Resource serveImg(String imgName) throws MalformedURLException, FileNotFoundException {
        Path path = Paths.get(UPLOAD_DIRECTORY).resolve(imgName);

        if (Files.notExists(path)) {
            throw new FileNotFoundException(imgName);
        }

        log.info("Image served: {}", imgName);

        return new UrlResource(path.toUri());
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
    public void enterCourse(Integer studentId, Integer courseId) throws IllegalArgumentException {
        Course course = courseDao.findById(courseId).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a course!"));
        Student student = findById(studentId);

        if (!course.getStudents().contains(student) || !student.getCourses().contains(course)) {
            course.getStudents().add(student);
            student.getCourses().add(course);

            entityManager.persist(course);
            entityManager.persist(student);

            log.info("{} added to {}", student, course);

        } else {
            throw new IllegalArgumentException("The student is already in this course!");
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
    public void leaveCourse(Integer studentId, Integer courseId) throws IllegalArgumentException {
        Course course = courseDao.findById(courseId).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a course!"));
        Student student = findById(studentId);

        if (course.getStudents().contains(student) && student.getCourses().contains(course)) {
            course.getStudents().remove(student);
            student.getCourses().remove(course);

            entityManager.persist(course);
            entityManager.persist(student);

            log.info("{} removed from {}", student, course);

        } else {
            throw new IllegalArgumentException("The student is not in this course!");
        }
    }

}
