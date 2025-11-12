package org.learning.studentManagement;

import jakarta.persistence.EntityManager;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.dataaccess.CourseDao;
import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.model.dto.CourseDto;
import org.learning.studentManagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CourseServiceTests {
    @Autowired
    private CourseService courseService;

    @MockitoBean
    private CourseDao courseDao;

    @MockitoBean
    private StudentDao studentDao;

    @MockitoBean
    EntityManager entityManager;

    private static Course createMockCourse() {
        Course course = new Course();
        course.setId(1);
        course.setName("courseName");
        course.setStartDate(LocalDate.parse("2025-01-01"));
        course.setEndDate(LocalDate.parse("2025-02-01"));
        course.setStudents(new ArrayList<>());

        return course;
    }

    @Test
    public void findAll_isOk() throws Exception {
        //setup
        when(courseDao.findAll()).thenReturn(new ArrayList<>());

        //execute
        assertEquals(new ArrayList<Course>(), courseService.findAll());
        verify(courseDao, times(1)).findAll();

        //cleanup
    }

    @Test
    public void findById_isOk() throws Exception {
        //setup
        Course course =  createMockCourse();
        when(courseDao.findById(1)).thenReturn(Optional.of(course));

        //execute
        Course sameCourse = courseService.findById(1);
        assertEquals(course, sameCourse);
        verify(courseDao, times(1)).findById(1);

        //cleanup
    }

    @Test
    public void findById_incorrectId_throwsException() throws Exception {
        //setup
        when(courseDao.findById(1)).thenReturn(Optional.empty());

        //execute
        assertThrows(IllegalArgumentException.class, () -> courseService.findById(1));
        verify(courseDao, times(1)).findById(1);

        //cleanup
    }

    @Test
    public void findByName_isOk() throws Exception {
        //setup
        Optional<Course> course =  Optional.of(createMockCourse());
        when(courseDao.findByName("courseName")).thenReturn(course);

        //execute
        assertEquals(course, courseService.findByName("courseName"));
        verify(courseDao, times(1)).findByName("courseName");

        //cleanup

    }

    @Test
    public void save_isOk() throws Exception {
        //setup
        when(courseDao.save(any(Course.class))).thenReturn(createMockCourse());
        when(courseDao.findByName("courseName")).thenReturn(Optional.empty());

        //execute
        Course course = courseService.save(new CourseDto(null, "courseName",
                LocalDate.parse("2025-01-01"), LocalDate.parse("2025-02-01"), new ArrayList<>()));
        assertEquals("courseName", course.getName());
        assertEquals(LocalDate.parse("2025-01-01"), course.getStartDate());
        assertEquals(LocalDate.parse("2025-02-01"), course.getEndDate());
        verify(courseDao, times(1)).save(any(Course.class));
        verify(courseDao, times(1)).findByName("courseName");

        //cleanup
    }

    @Test
    public void save_endDateBeforeStartDate_throwsException() throws Exception {
        //setup
        when(courseDao.findByName("courseName")).thenReturn(Optional.empty());

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () -> courseService.save(
                new CourseDto(null, "courseName", LocalDate.parse("2025-02-01"), LocalDate.parse("2025-01-01"), new ArrayList<>())));
        assertEquals("The given start date is after the given end date!", exception.getMessage());
        verify(courseDao, times(1)).findByName("courseName");
        verify(courseDao, never()).save(any(Course.class));

        //cleanup
    }

    @Test
    public void update_isOk() throws Exception {
        //setup
        Course course = new Course();
        when(courseDao.findById(1)).thenReturn(Optional.of(course));
        doNothing().when(courseDao).update(any(Course.class));

        //execute
        courseService.update(new CourseDto("1", "courseName",
                LocalDate.parse("2025-01-01"), LocalDate.parse("2025-02-01"), new ArrayList<>()));
        assertEquals("courseName", course.getName());
        assertEquals(LocalDate.parse("2025-01-01"), course.getStartDate());
        assertEquals(LocalDate.parse("2025-02-01"), course.getEndDate());
        verify(courseDao, times(1)).findById(1);
        verify(courseDao, times(1)).update(any(Course.class));

        //cleanup
    }

    @Test
    public void update_endDateBeforeStartDate_throwsException() throws Exception {
        //setup
        Course course = createMockCourse();
        when(courseDao.findById(1)).thenReturn(Optional.of(course));

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () -> courseService.update(new CourseDto("1",
                "courseName", null, LocalDate.parse("2024-02-01"), new ArrayList<>())));
        assertEquals("The given start date is after the given end date!", exception.getMessage());
        verify(courseDao, times(1)).findById(1);
        verify(courseDao, never()).update(any(Course.class));

        //cleanup
    }

    @Test
    public void delete_isOk() throws Exception {
        //setup
        Course course =  createMockCourse();
        when(courseDao.findById(1)).thenReturn(Optional.of(course));
        doNothing().when(courseDao).delete(course);

        //execute
        assertDoesNotThrow(() -> courseService.delete(1));
        verify(courseDao, times(1)).findById(1);
        verify(courseDao, times(1)).delete(course);

        //cleanup
    }

    @Test
    public void delete_incorrectId_throwsException() throws Exception {
        //setup
        when(courseDao.findById(1)).thenReturn(Optional.empty());

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                courseService.delete(1));
        assertEquals("The given ID isn't associated with a course!", exception.getMessage());
        verify(courseDao, times(1)).findById(1);
        verify(courseDao, never()).delete(any(Course.class));

        //cleanup
    }

    private static Pair<Student, Course> createMockStudentAndCourse(boolean connected) {
        Student student = new Student();
        student.setId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setEmail("email@email.email");
        student.setCnp("1234567890123");
        student.setImgName("1234567890123.png");

        Group group = new Group();
        group.setId(1);
        group.setName("groupName");
        group.setStudents(new ArrayList<>(List.of(student)));
        student.setGroup(group);

        student.setCourses(new ArrayList<>());

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
    public void addStudent_isOk() throws Exception {
        //setup
        Pair<Student, Course> studentAndCourse = createMockStudentAndCourse(false);
        Student student = studentAndCourse.getFirst();
        Course course = studentAndCourse.getSecond();

        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        when(courseDao.findById(1)).thenReturn(Optional.of(course));
        doNothing().when(entityManager).persist(any(Student.class));
        doNothing().when(entityManager).persist(any(Course.class));

        //execute
        courseService.addStudent(1, 1);
        assertEquals(1, student.getCourses().get(0).getId());
        assertEquals(1, course.getStudents().get(0).getId());
        verify(studentDao, times(1)).findById(1);
        verify(courseDao, times(1)).findById(1);
        verify(entityManager, times(1)).persist(any(Student.class));
        verify(entityManager, times(1)).persist(any(Course.class));

        //cleanup
    }

    @Test
    public void addStudent_alreadyPresent_isOk() throws Exception {
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
                courseService.addStudent(1, 1));
        assertEquals("The given student is part of the course!", exception.getMessage());

        //cleanup
    }

    @Test
    public void removeStudent_isOk() throws Exception {
        //setup
        Pair<Student, Course> studentAndCourse = createMockStudentAndCourse(true);
        Student student = studentAndCourse.getFirst();
        Course course = studentAndCourse.getSecond();

        when(studentDao.findById(1)).thenReturn(Optional.of(student));
        when(courseDao.findById(1)).thenReturn(Optional.of(course));
        doNothing().when(entityManager).persist(any(Student.class));
        doNothing().when(entityManager).persist(any(Course.class));

        //execute
        courseService.removeStudent(1, 1);
        assertEquals(0, student.getCourses().size());
        assertEquals(0, course.getStudents().size());
        verify(studentDao, times(1)).findById(1);
        verify(courseDao, times(1)).findById(1);
        verify(entityManager, times(1)).persist(any(Student.class));
        verify(entityManager, times(1)).persist(any(Course.class));

        //cleanup
    }

    @Test
    public void removeStudent_notPresent_isOk() throws Exception {
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
                courseService.removeStudent(1, 1));
        assertEquals("The given student isn't part of the course!", exception.getMessage());

        //cleanup
    }

}
