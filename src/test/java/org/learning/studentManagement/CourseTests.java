package org.learning.studentManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.service.CourseService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = {"USER"})
    void coursePage_noFilters_isOk() throws Exception {
        //execution
        mockMvc.perform(get("/course"))
                .andExpect(status().isOk())
                .andExpect(view().name("course"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addCourse_isOk() throws Exception {
        //execution
        mockMvc.perform(post("/course/add")
                        .param("name", "courseName")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());

        //cleanup
        courseService.findByName("courseName").ifPresent(course -> courseService.delete(course.getId()));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteCourse_isOk() throws Exception {
        //setup
        Course course = courseService.save("courseName", LocalDate.now(), LocalDate.now().plusDays(1));
        String courseId = String.valueOf(course.getId());

        //execution
        mockMvc.perform(post("/course/delete")
                        .param("id", courseId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterCourse_isOk() throws Exception {
        //setup
        Course course = courseService.save("courseName", LocalDate.now(), LocalDate.now().plusDays(1));
        String courseId = String.valueOf(course.getId());

        //execution
        mockMvc.perform(post("/course/alter")
                        .param("id", courseId)
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());

        //cleanup
        courseService.findByName("courseName")
                .ifPresent(newCourse -> courseService.delete(newCourse.getId()));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addStudent_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        Student student = studentService.save("firstName",
                "lastName", "email@email.email", "1234512345123", "groupName", file);
        String studentId = String.valueOf(student.getId());
        Course course = courseService.save("courseName", LocalDate.now(), LocalDate.now().plusDays(1));
        String courseId = String.valueOf(course.getId());

        //execution
        mockMvc.perform(post("/course/addStudent")
                        .param("courseId", courseId)
                        .param("studentId", studentId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());

        //cleanup
        studentService.leaveCourse(student.getId(), course.getId());
        studentService.delete(studentId);
        courseService.delete(course.getId());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void removeStudent_isOk() throws Exception {
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
        mockMvc.perform(post("/course/removeStudent")
                        .param("courseId", courseId)
                        .param("studentId", studentId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());

        //cleanup
        studentService.delete(studentId);
        courseService.delete(course.getId());
    }
















}
