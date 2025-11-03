package org.learning.studentManagement;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.dataaccess.CourseDao;
import org.learning.studentManagement.dataaccess.GroupDao;
import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.service.CourseService;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StudentServiceTests {
    @MockitoBean
    StudentDao studentDao;

    @MockitoBean
    GroupDao groupDao;

    @MockitoBean
    CourseDao courseDao;

    @MockitoBean
    EntityManager entityManager;

    @Autowired
    StudentService studentService;

    private static Student createMockStudent(boolean addGroup, boolean addCourse) {
        Student student = new Student();
        student.setId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setEmail("email@email.email");
        student.setCnp("1234567890123");
        student.setImgName("1234567890123.png");

        if (addGroup) {
            Group group = new Group();
            group.setId(1);
            group.setName("groupName");
            group.setStudents(new ArrayList<>(List.of(student)));
            student.setGroup(group);
        }

        if (addCourse) {
            Course course = new Course();
            course.setId(1);
            course.setName("courseName");
            course.setStartDate(LocalDate.parse("2025-01-01"));
            course.setEndDate(LocalDate.parse("2025-02-01"));
//            course.setStudents(new ArrayList<>(List.of(student)));
            student.setCourses(new ArrayList<>(List.of(course)));
        }

        return student;
    }

    private static Student createMockStudent() {
        return createMockStudent(false, false);
    }

    @Test
    public void findAll_isOk() throws Exception {
        //setup
        when(studentDao.findAll()).thenReturn(new ArrayList<>());

        //execute
        assertEquals(new ArrayList<>(),  studentService.findAll());
        verify(studentDao, times(1)).findAll();

        //cleanup
    }

    @Test
    public void findById_isOk() throws Exception {
        //setup
        Student student = createMockStudent();
        when(studentDao.findById(1)).thenReturn(Optional.of(student));

        //execute
        assertEquals(student, studentService.findById(1));
        verify(studentDao, times(1)).findById(1);

        //cleanup
    }

    @Test
    public void findById_incorrectId_throwsException() throws Exception {
        //setup
        when(studentDao.findById(1)).thenReturn(Optional.empty());

        //execute
        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> studentService.findById(1));
        assertEquals("The given ID isn't associated with a student!", exception.getMessage());
        verify(studentDao, times(1)).findById(1);

        //cleanup
    }

    @Test
    public void findByCnp_isOk() throws Exception {
        //setup
        Optional<Student> studentOptional = Optional.of(createMockStudent());
        when(studentDao.findByCnp("1234567890123")).thenReturn(studentOptional);

        //execute
        assertEquals(studentOptional, studentService.findByCnp("1234567890123"));
        verify(studentDao, times(1)).findByCnp("1234567890123");

        //cleanup
    }

    @Test
    public void findByEmail_isOk() throws Exception {
        //setup
        Optional<Student> studentOptional = Optional.of(createMockStudent());
        when(studentDao.findByEmail("email@email.email")).thenReturn(studentOptional);

        //execute
        assertEquals(studentOptional, studentService.findByEmail("email@email.email"));
        verify(studentDao, times(1)).findByEmail("email@email.email");

        //cleanup
    }

    private static void assertStudentProperties(Student oldStudent, Student newStudent) {
        assertEquals(oldStudent.getId(), newStudent.getId());
        assertEquals(oldStudent.getFirstName(), newStudent.getFirstName());
        assertEquals(oldStudent.getLastName(), newStudent.getLastName());
        assertEquals(oldStudent.getEmail(), newStudent.getEmail());
        assertEquals(oldStudent.getCnp(), newStudent.getCnp());
        assertEquals(oldStudent.getGroup(), newStudent.getGroup());
        assertEquals(oldStudent.getCourses(), newStudent.getCourses());
    }

    @Test
    public void save_isOk() throws Exception {
        //setup
        MockMultipartFile mockFile = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());

        Student student = createMockStudent(false, false);
        student.setId(0);
        Group group = new Group();
        group.setId(1);
        group.setName("otherGroup");

        when(studentDao.findByEmail("email@email.email")).thenReturn(Optional.empty());
        when(studentDao.findByCnp("1234567890123")).thenReturn(Optional.empty());
        when(studentDao.save(any(Student.class))).thenReturn(student);
        doNothing().when(studentDao).update(any(Student.class));
        when(groupDao.save(any(Group.class))).thenReturn(group);
        when(groupDao.findByName("groupName")).thenReturn(Optional.empty());

        //execute
        Student savedStudent = studentService.save("firstName", "lastName",
                "email@email.email", "1234567890123", "groupName", mockFile);

        assertEquals(0, savedStudent.getId());
        assertEquals("firstName", savedStudent.getFirstName());
        assertEquals("lastName", savedStudent.getLastName());
        assertEquals("email@email.email", savedStudent.getEmail());
        assertEquals("1234567890123", savedStudent.getCnp());

        verify(studentDao, times(1)).findByEmail("email@email.email");
        verify(studentDao, times(1)).findByCnp("1234567890123");
        verify(studentDao, times(1)).save(any(Student.class));
        verify(studentDao, times(1)).update(any(Student.class));
        verify(groupDao, times(2)).save(any(Group.class));
        verify(groupDao, times(1)).findByName("groupName");

        //cleanup
    }

    @Test
    public void save_emptyFile_ThrowsException() throws Exception {
        //setup
        MockMultipartFile mockFile = new MockMultipartFile("profilePic", new byte[]{});

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.save("firstName","lastName","email@email.email",
                        "1234567890123", "groupName", mockFile));
        assertEquals("No picture has been uploaded!", exception.getMessage());
        verify(studentDao, never()).save(any(Student.class));
        verify(studentDao, never()).update(any(Student.class));
        verify(studentDao, never()).findByEmail(any(String.class));
        verify(studentDao, never()).findByCnp(any(String.class));
        verify(groupDao, never()).save(any(Group.class));

        //cleanup
    }

    @Test
    public void update_isOk() throws Exception {
        //setup
        MockMultipartFile mockFile = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = new Student();
        student.setId(1);
        Group newGroup = new Group();
        newGroup.setId(1);
        newGroup.setName("newGroup");

        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        when(studentDao.findByCnp("1234567890123")).thenReturn(Optional.empty());
        when(studentDao.findByEmail("email@email.email")).thenReturn(Optional.empty());
        when(groupDao.findById(1)).thenReturn(Optional.of(newGroup));
        doNothing().when(studentDao).update(any(Student.class));

        //execute
        studentService.update("1", "firstName", "lastName",
                "email@email.email", "1234567890123", "1", mockFile);

        assertEquals(1, student.getId());
        assertEquals("firstName", student.getFirstName());
        assertEquals("lastName", student.getLastName());
        assertEquals("email@email.email", student.getEmail());
        assertEquals("1234567890123", student.getCnp());
        assertEquals("1234567890123.png", student.getImgName());
        assertEquals(1, student.getGroup().getId());
        assertEquals("newGroup", student.getGroup().getName());

        verify(studentDao, times(1)).findById(1);
        verify(studentDao, times(1)).findByCnp("1234567890123");
        verify(studentDao, times(1)).findByEmail("email@email.email");
        verify(studentDao, times(1)).update(any(Student.class));
        verify(groupDao, times(1)).findById(1);

        //cleanup
    }

    @Test
    public void update_duplicateEmail_throwsException() throws Exception {
        //setup
        MockMultipartFile mockFile = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = createMockStudent();

        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        when(studentDao.findByEmail("email@email.email")).thenReturn(Optional.of(new Student()));

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.update("1", "firstName", "lastName",
                        "email@email.email", "1234567890123", "1", mockFile));
        assertEquals("Student Email already taken!", exception.getMessage());
        verify(studentDao, times(1)).findById(1);
        verify(studentDao, never()).findByCnp(any());
        verify(studentDao, times(1)).findByEmail(any());
        verify(groupDao, never()).findById(anyInt());

        //cleanup
    }

    @Test
    public void delete_isOk() throws Exception {
        //setup
        Student student = createMockStudent();
        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        doNothing().when(studentDao).delete(student);

        //execute
        assertDoesNotThrow(() -> studentService.delete("1"));
        verify(studentDao, times(1)).findById(1);
        verify(studentDao, times(1)).delete(student);

        //cleanup
    }

    @Test
    public void delete_incorrectId_throwsException() throws Exception {
        //setup
        when(studentDao.findById(1)).thenReturn(Optional.empty());

        //execute
        assertThrows(IllegalArgumentException.class, () -> studentService.delete("1"));
        verify(studentDao, times(1)).findById(1);
        verify(studentDao, never()).delete(any(Student.class));

        //cleanup
    }

    @Test
    public void serveImg_isOk() throws Exception {
        //setup
        MockMultipartFile mockFile = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/imgs";
        Path imgPath = Paths.get(UPLOAD_DIRECTORY, "1234567890123.png");
        Files.write(imgPath, mockFile.getBytes());

        //execute
        assertDoesNotThrow(() -> studentService.serveImg("1234567890123.png"));

        //cleanup
    }

    @Test
    public void serveImg_badName_throwsException() throws Exception {
        //setup
        String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/imgs";
        Path imgPath = Paths.get(UPLOAD_DIRECTORY, "1234567890123.png");
        if(Files.exists(imgPath)) {
            Files.delete(imgPath);
        }


        //execute
        assertThrows(FileNotFoundException.class, () -> studentService.serveImg("1234567890123.png"));

        //cleanup
    }

    private static Pair<Student, Course> createMockStudentAndCourse(boolean connected) {
        Student student = createMockStudent();

        Course course = new Course();
        course.setId(1);
        course.setName("courseName");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusDays(1));
        if(connected) {
            course.setStudents(new ArrayList<>(List.of(student)));
            student.setCourses(new ArrayList<>(List.of(course)));
        } else {
            course.setStudents(new ArrayList<>());
            student.setCourses(new ArrayList<>());
        }


        return  Pair.of(student, course);
    }

    @Test
    public void enterCourse_isOk() throws Exception {
        //setup
        Pair<Student, Course> studentAndCourse = createMockStudentAndCourse(false);
        Student student = studentAndCourse.getFirst();
        Course course = studentAndCourse.getSecond();

        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        when(courseDao.findById(1)).thenReturn(Optional.of(course));
        doNothing().when(entityManager).persist(any(Student.class));
        doNothing().when(entityManager).persist(any(Course.class));

        //execute
        studentService.enterCourse(1, 1);
        assertEquals(1, student.getCourses().get(0).getId());
        assertEquals(1, course.getStudents().get(0).getId());
        verify(studentDao, times(1)).findById(1);
        verify(courseDao, times(1)).findById(1);
        verify(entityManager, times(1)).persist(any(Student.class));
        verify(entityManager, times(1)).persist(any(Course.class));

        //cleanup
    }

    @Test
    public void enterCourse_alreadyConnected_throwsException() throws Exception {
        //setup
        Pair<Student, Course> studentAndCourse = createMockStudentAndCourse(true);
        Student student = studentAndCourse.getFirst();
        Course course = studentAndCourse.getSecond();

        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        when(courseDao.findById(1)).thenReturn(Optional.of(course));
        doNothing().when(entityManager).persist(any(Student.class));
        doNothing().when(entityManager).persist(any(Course.class));

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.enterCourse(1, 1));
        assertEquals("The student is already in this course!", exception.getMessage());


        //cleanup
    }

    @Test
    public void leaveCourse_isOk() throws Exception {
        //setup
        Pair<Student, Course> studentAndCourse = createMockStudentAndCourse(true);
        Student student = studentAndCourse.getFirst();
        Course course = studentAndCourse.getSecond();

        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        when(courseDao.findById(1)).thenReturn(Optional.of(course));
        doNothing().when(entityManager).persist(any(Student.class));
        doNothing().when(entityManager).persist(any(Course.class));

        //execute

        studentService.leaveCourse(1, 1);
        assertEquals(0, student.getCourses().size());
        assertEquals(0, course.getStudents().size());
        verify(studentDao, times(1)).findById(1);
        verify(courseDao, times(1)).findById(1);
        verify(entityManager, times(1)).persist(any(Student.class));
        verify(entityManager, times(1)).persist(any(Course.class));

        //cleanup
    }

    @Test
    public void leaveCourse_notConnected_throwsException() throws Exception {
        //setup
        Pair<Student, Course> studentAndCourse = createMockStudentAndCourse(false);
        Student student = studentAndCourse.getFirst();
        Course course = studentAndCourse.getSecond();

        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        when(courseDao.findById(1)).thenReturn(Optional.of(course));
        doNothing().when(entityManager).persist(any(Student.class));
        doNothing().when(entityManager).persist(any(Course.class));

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.leaveCourse(1, 1));
        assertEquals("The student is not in this course!", exception.getMessage());

        //cleanup
    }

}
