package org.learning.studentManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.service.CourseService;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private CourseService courseService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void studentPage_noFilters_isOk() throws Exception {
        mockMvc.perform(get("/student"))
                .andExpect(status().isOk()).andExpect(view().name("student"));
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void studentPage_withFilters_isOk() throws Exception {
        mockMvc.perform(get("/student?sortBy=firstName&pageNum=1&selectedGroup=1"))
                .andExpect(status().isOk()).andExpect(view().name("student"));
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void oneStudentPage_isOk() throws Exception {
        //execution
        mockMvc.perform(get("/student/1")).andExpect(status().isOk())
                .andExpect(view().name("oneStudent"));
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void oneStudentPage_wrongId_is4xx() throws Exception {
        //execution
        mockMvc.perform(get("/student/99")).andExpect(status().is4xxClientError())
                .andExpect(view().name("error"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addStudent_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());

        //execution
        mockMvc.perform(multipart("/student/add")
                .file(file)
                .param("firstName", "firstName")
                .param("lastName", "lastName")
                .param("email", "email@email.email")
                .param("cnp", "1234512345123")
                .param("groupName", "groupName")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/student"))
        .andDo(print());

        //cleanup
        studentService.findByCnp("1234512345123")
                .ifPresent(student -> studentService.delete(String.valueOf(student.getId())));
        groupService.findByName("groupName")
                .ifPresent(group -> groupService.delete(String.valueOf(group.getId())));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addStudent_fileMissing_is4xx() throws Exception {
        //execution
        mockMvc.perform(multipart("/student/add")
                        .param("firstName", "firstName")
                        .param("lastName", "lastName")
                        .param("email", "email@email.email")
                        .param("cnp", "1234512345123")
                        .param("groupName", "groupName")
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteStudent_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = studentService.save("firstName",
                "lastName", "email@email.email", "1234512345123", "groupName", file);
        String studentId = String.valueOf(student.getId());

        //execution
        mockMvc.perform(post("/student/delete")
                        .param("id", studentId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteStudent_idNotANumber_isOk() throws Exception {
        //execution
        mockMvc.perform(post("/student/delete")
                        .param("id", "NotANumber")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterStudent_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = studentService.save("firstName",
                "lastName", "email@email.email", "1234512345123", "groupName", file);
        String studentId = String.valueOf(student.getId());
        MockMultipartFile newFile = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/pic.png".getBytes());

        //execution
        mockMvc.perform(multipart("/student/alter")
                        .file(newFile)
                        .param("id", studentId)
                        .param("firstName", "newFirstName")
                        .param("lastName", "newLastName")
                        .param("email", "newEmail@email.email")
                        .param("cnp", "1234512345124")
                        .param("groupName", "newGroupName")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"))
                .andDo(print());

        //cleanup
        studentService.findByCnp("1234512345124")
                .ifPresent(student1 -> studentService.delete(String.valueOf(student1.getId())));
        groupService.findByName("newGroupName")
                .ifPresent(group -> groupService.delete(String.valueOf(group.getId())));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterStudent_duplicateCnp_is4xxx() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = studentService.save("firstName",
                "lastName", "email@email.email", "1234512345123", "groupName", file);
        String studentId = String.valueOf(student.getId());
        MockMultipartFile newFile = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/pic.png".getBytes());

        //execution
        mockMvc.perform(multipart("/student/alter")
                        .file(newFile)
                        .param("id", studentId)
                        .param("firstName", "newFirstName")
                        .param("lastName", "newLastName")
                        .param("email", "newEmail@email.email")
                        .param("cnp", "0000000000001")
                        .param("groupName", "newGroupName")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());

        //cleanup
        studentService.findByCnp("1234512345123")
                .ifPresent(student1 -> studentService.delete(String.valueOf(student1.getId())));
        groupService.findByName("groupName")
                .ifPresent(group -> groupService.delete(String.valueOf(group.getId())));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void enterCourse_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = studentService.save("firstName",
                "lastName", "email@email.email", "1234512345123", "groupName", file);
        String studentId = String.valueOf(student.getId());
        Course course = courseService.save("courseName", LocalDate.now(), LocalDate.now().plusDays(1));
        String courseId = String.valueOf(course.getId());

        //execution
        mockMvc.perform(post("/student/enterCourse")
                        .param("courseId", courseId)
                        .param("studentId", studentId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/" + studentId))
                .andDo(print());

        //cleanup
        studentService.leaveCourse(student.getId(), course.getId());
        studentService.delete(studentId);
        courseService.delete(course.getId());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void enterCourse_duplicateConnection_is4xx() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = studentService.save("firstName",
                "lastName", "email@email.email", "1234512345123", "groupName", file);
        String studentId = String.valueOf(student.getId());
        Course course = courseService.save("courseName", LocalDate.now(), LocalDate.now().plusDays(1));
        String courseId = String.valueOf(course.getId());
        studentService.enterCourse(student.getId(), course.getId());

        //execution
        mockMvc.perform(post("/student/enterCourse")
                        .param("courseId", courseId)
                        .param("studentId", studentId)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());

        //cleanup
        studentService.leaveCourse(student.getId(), course.getId());
        studentService.delete(studentId);
        courseService.delete(course.getId());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void leaveCourse_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = studentService.save("firstName",
                "lastName", "email@email.email", "1234512345123", "groupName", file);
        String studentId = String.valueOf(student.getId());
        Course course = courseService.save("courseName", LocalDate.now(), LocalDate.now().plusDays(1));
        String courseId = String.valueOf(course.getId());
        studentService.enterCourse(student.getId(), course.getId());

        //execution
        mockMvc.perform(post("/student/leaveCourse")
                        .param("courseId", courseId)
                        .param("studentId", studentId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/" + studentId))
                .andDo(print());

        //cleanup
        studentService.delete(studentId);
        courseService.delete(course.getId());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void leaveCourse_incorrectCourseId_is4xx() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = studentService.save("firstName",
                "lastName", "email@email.email", "1234512345123", "groupName", file);
        String studentId = String.valueOf(student.getId());
        String falseStudentId = String.valueOf(student.getId() * 100000);
        Course course = courseService.save("courseName", LocalDate.now(), LocalDate.now().plusDays(1));
        String courseId = String.valueOf(course.getId());
        studentService.enterCourse(student.getId(), course.getId());

        //execution
        mockMvc.perform(post("/student/leaveCourse")
                        .param("courseId", courseId)
                        .param("studentId", falseStudentId)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());

        //cleanup
        studentService.leaveCourse(student.getId(), course.getId());
        studentService.delete(studentId);
        courseService.delete(course.getId());
    }

}
