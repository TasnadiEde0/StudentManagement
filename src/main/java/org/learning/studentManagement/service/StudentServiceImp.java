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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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


    String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/imgs";

    private void emailDuplicateCheck(String email) {
        Optional<Student> testEmail = studentDao.findByEmail(email);
        if (testEmail.isPresent()) {
            throw new IllegalArgumentException("Student Email already taken!");
        }
    }

    private void cnpDuplicateCheck(String cnp) {
        Optional<Student> testCnp = studentDao.findByCnp(cnp);
        if (testCnp.isPresent()) {
            throw new IllegalArgumentException("Student CNP already taken!");
        }
    }

    private Group fetchGroup(String name) {
        Group group = groupDao.findByName(name).orElse(null);

        if (group == null) {
            group = new Group();
            group.setName(name);
        }

        group = groupDao.save(group);

        return group;

    }

    @Override
    public Student findById(int Id) {
        Student student = studentDao.findById(Id).orElse(null);

        if (student == null) {
            throw new IllegalArgumentException("The given ID isn't associated with a student!");
        }

        return student;
    }

    @Override
    public Optional<Student> findByCnp(String cnp) {
        return studentDao.findByCnp(cnp);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return studentDao.findByEmail(email);
    }

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    @Transactional
    public Student save(String firstName, String lastName, String email, String cnp, String groupName, MultipartFile file) throws IOException {

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

        return student;

    }

    /**
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param email
     * @param cnp
     * @param groupid
     * @param file
     * @throws IOException DOES NOT UPDATE COURSE LIST
     */
    @Override
    @Transactional
    public void update(String id, String firstName, String lastName, String email, String cnp, String groupid, MultipartFile file) throws IOException {
        Student student = findById(Integer.parseInt(id));

        if (!firstName.isEmpty()) {
            student.setFirstName(firstName);
        }
        if (!lastName.isEmpty()) {
            student.setLastName(lastName);
        }
        if (!email.isEmpty()) {
            student.setEmail(email);
        }
        if (!cnp.isEmpty()) {
            student.setCnp(cnp);
        }
        if (!groupid.isEmpty()) {
            student.setGroup(groupDao.findById(Integer.parseInt(groupid)).orElse(null));
        }

        if (!file.isEmpty()) {
            Path imgPath = Paths.get(UPLOAD_DIRECTORY, student.getCnp() + "_" + ".png");
            Files.write(imgPath, file.getBytes());
            student.setImgName(student.getCnp() + "_" +
                    student.getFirstName() + ".png");
        }

        studentDao.update(student);

    }

    @Override
    @Transactional
    public void delete(String id) {
        Student student = findById(Integer.parseInt(id));

        studentDao.delete(student);

    }

    @Override
    public Resource serveImg(String imgName) throws MalformedURLException, FileNotFoundException {
        Path path = Paths.get(UPLOAD_DIRECTORY).resolve(imgName);

        if (Files.notExists(path)) {
            throw new FileNotFoundException(imgName);
        }

        return new UrlResource(path.toUri());
    }

    @Override
    @Transactional
    public void enterCourse(Integer studentId, Integer courseId) {
        Course course = courseDao.findById(courseId).orElseThrow(() -> new IllegalArgumentException("The given ID isn't associated with a course!"));
        Student student = findById(studentId);

        if (!course.getStudents().contains(student) || !student.getCourses().contains(course)) {
            course.getStudents().add(student);
            student.getCourses().add(course);

            entityManager.persist(course);
            entityManager.persist(student);

        } else {
            throw new IllegalArgumentException("The student is already in this course!");
        }
    }

    @Override
    @Transactional
    public void leaveCourse(Integer studentId, Integer courseId) {
        Course course = courseDao.findById(courseId).orElseThrow(() -> new IllegalArgumentException("The given ID isn't associated with a course!"));
        Student student = findById(studentId);

        if (course.getStudents().contains(student) && student.getCourses().contains(course)) {
            course.getStudents().remove(student);
            student.getCourses().remove(course);

            entityManager.persist(course);
            entityManager.persist(student);

        } else {
            throw new IllegalArgumentException("The student is already in this course!");
        }
    }

}
