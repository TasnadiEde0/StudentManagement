package org.learning.studentManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.service.CourseService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private StudentService studentService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void coursePage_isOk() throws Exception {
        //setup
        when(courseService.findAll()).thenReturn(new ArrayList<>());

        //execution
        mockMvc.perform(get("/course"))
                .andExpect(status().isOk())
                .andExpect(view().name("course"));

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addCourse_isOk() throws Exception {
        //setup
        when(courseService.save("courseName", LocalDate.parse("2025-01-01"),
                LocalDate.parse("2025-12-31"))).thenReturn(new Course());

        //execution
        mockMvc.perform(post("/course/add")
                        .param("name", "courseName")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());
        verify(courseService, times(1)).save("courseName",
                LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31"));

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addCourse_endDateAfterStartDate_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(courseService).save("courseName",
                LocalDate.parse("2025-12-31"), LocalDate.parse("2025-01-01"));

        //execution
        mockMvc.perform(post("/course/add")
                        .param("name", "courseName")
                        .param("endDate", "2025-01-01")
                        .param("startDate", "2025-12-31")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(courseService, times(1)).save("courseName",
                LocalDate.parse("2025-12-31"), LocalDate.parse("2025-01-01"));

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteCourse_isOk() throws Exception {
        //setup
        doNothing().when(studentService).delete("1");

        //execution
        mockMvc.perform(post("/course/delete")
                        .param("id", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteCourse_incorrectlyIntroducedId_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(courseService).delete(-1);


        //execution
        mockMvc.perform(post("/course/delete")
                        .param("id", "-1")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterCourse_isOk() throws Exception {
        //setup
        doNothing().when(courseService).update(1, "courseName", LocalDate.parse("2025-01-01"),
                LocalDate.parse("2025-12-31"));

        //execution
        mockMvc.perform(post("/course/alter")
                        .param("id", "1")
                        .param("name", "courseName")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());
        verify(courseService, times(1)).update(1, "courseName",
                LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31"));

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterCourse_newEndDateBeforeStartDate_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(courseService).update(1, null, null,
                LocalDate.parse("2024-12-31"));

        //execution
        mockMvc.perform(post("/course/alter")
                        .param("id", "1")
                        .param("endDate", "2024-12-31")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"));
        verify(courseService, times(1)).update(1, null, null,
                LocalDate.parse("2024-12-31"));

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addStudent_isOk() throws Exception {
        //setup
        doNothing().when(courseService).addStudent(1, 1);

        //execution
        mockMvc.perform(post("/course/addStudent")
                        .param("courseId", "1")
                        .param("studentId", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());
        verify(courseService, times(1)).addStudent(1, 1);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addStudent_alreadyAttending_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(courseService).addStudent(1, 1);

        //execution
        mockMvc.perform(post("/course/addStudent")
                        .param("courseId", "1")
                        .param("studentId", "1")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(courseService, times(1)).addStudent(1, 1);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void removeStudent_isOk() throws Exception {
        //setup
        doNothing().when(courseService).removeStudent(1, 1);

        //execution
        mockMvc.perform(post("/course/removeStudent")
                        .param("courseId", "1")
                        .param("studentId", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"))
                .andDo(print());
        verify(courseService, times(1)).removeStudent(1, 1);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void removeStudent_incorrectIds_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(courseService).removeStudent(1, 1);

        //execution
        mockMvc.perform(post("/course/removeStudent")
                        .param("courseId", "1")
                        .param("studentId", "1")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(courseService, times(1)).removeStudent(1, 1);

        //cleanup
    }

}
