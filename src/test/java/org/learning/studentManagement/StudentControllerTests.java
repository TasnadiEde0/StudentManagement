package org.learning.studentManagement;

import jakarta.persistence.EntityManager;
import org.glassfish.jaxb.core.v2.TODO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.service.CourseService;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.postgresql.hostchooser.HostRequirement.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private EntityManager entityManager;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private static Student createMockStudent() {
        Student student = new Student();
        student.setId(1);
        student.setFirstName("firstName");
        student.setLastName("lastName");
        student.setEmail("email@email.email");
        student.setCnp("1234512345123");
        Group group = new Group();
        group.setId(1);
        group.setName("groupName");
        group.setStudents(new ArrayList<>(List.of(student)));
        student.setGroup(group);

        return student;
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void studentPage_noFilters_isOk() throws Exception {
        //setup
        when(studentService.findAll()).thenReturn(new ArrayList<>());

        //execution
        mockMvc.perform(get("/student"))
                .andExpect(status().isOk()).andExpect(view().name("student"));
        verify(studentService, times(1)).findAll();

        //cleanup
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void studentPage_withFilters_isOk() throws Exception {
        //setup
        when(studentService.findAll()).thenReturn(new ArrayList<>());


        mockMvc.perform(get("/student?sortBy=firstName&pageNum=1&selectedGroup=0"))
                .andExpect(status().isOk()).andExpect(view().name("student"));
        verify(studentService, times(1)).findAll();

        //cleanup
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void oneStudentPage_isOk() throws Exception {
        //setup
        when(studentService.findById(1)).thenReturn(createMockStudent());

        //execution
        mockMvc.perform(get("/student/" + 1)).andExpect(status().isOk())
                .andExpect(view().name("oneStudent"));
        verify(studentService, times(1)).findById(1);

        //cleanup
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void oneStudentPage_wrongId_is4xx() throws Exception {
        //setup
        when(studentService.findById(2)).thenThrow(IllegalArgumentException.class);

        //execution
        mockMvc.perform(get("/student/2"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"));
        verify(studentService, times(1)).findById(2);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addStudent_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        when(studentService.save("firstName", "lastName", "email@email.email",
                "1234512345123", "groupName", file)).thenReturn(new Student());

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
        verify(studentService, times(1)).save("firstName", "lastName",
                "email@email.email", "1234512345123", "groupName", file);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addStudent_fileMissing_is4xx() throws Exception {
        //setup

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
        verify(studentService, never()).save(any(), any(), any(), any(), any(), any());

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteStudent_isOk() throws Exception {
        //setup
        doNothing().when(studentService).delete("1");

        //execution
        mockMvc.perform(post("/student/delete")
                        .param("id", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"))
                .andDo(print());
        verify(studentService, times(1)).delete("1");

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteStudent_idNotANumber_isOk() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(studentService).delete("NotANumber");

        //execution
        mockMvc.perform(post("/student/delete")
                        .param("id", "NotANumber")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(studentService, times(1)).delete("NotANumber");

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterStudent_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        doNothing().when(studentService).update("1", "firstName", "lastName",
                "email@email.email", "1234512345123", "groupId", file);

        //execution
        mockMvc.perform(multipart("/student/alter")
                        .file(file)
                        .param("id", "1")
                        .param("firstName", "firstName")
                        .param("lastName", "lastName")
                        .param("email", "email@email.email")
                        .param("cnp", "1234512345123")
                        .param("groupid", "groupId")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"))
                .andDo(print());
        verify(studentService, times(1)).update("1", "firstName",
                "lastName", "email@email.email", "1234512345123", "groupId", file);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterStudent_duplicateCnp_is4xxx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(studentService).update("1", "", "",
                "", "1234512345123", "", null);

        //execution
        mockMvc.perform(multipart("/student/alter")
                        .param("id", "1")
                        .param("cnp", "1234512345123")
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
        verify(studentService, times(1)).update("1", "", "",
                "", "1234512345123", "", null);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void enterCourse_isOk() throws Exception {
        //setup
        doNothing().when(studentService).enterCourse(1, 1);

        //execution
        mockMvc.perform(post("/student/enterCourse")
                        .param("courseId", "1")
                        .param("studentId", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/1"))
                .andDo(print());
        verify(studentService, times(1)).enterCourse(1, 1);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void enterCourse_duplicateConnection_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(studentService).enterCourse(1, 1);

        //execution
        mockMvc.perform(post("/student/enterCourse")
                        .param("courseId", "1")
                        .param("studentId", "1")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(studentService, times(1)).enterCourse(1, 1);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin") //TODO
    void leaveCourse_isOk() throws Exception {
        //setup
        doNothing().when(studentService).leaveCourse(1, 1);

        //execution
        mockMvc.perform(post("/student/leaveCourse")
                        .param("courseId", "1")
                        .param("studentId", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/1"))
                .andDo(print());
        verify(studentService, times(1)).leaveCourse(1, 1);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void leaveCourse_incorrectCourseId_is4xx() throws Exception { //TODO
        //setup
        doThrow(IllegalArgumentException.class).when(studentService).leaveCourse(1, 1);

        //execution
        mockMvc.perform(post("/student/leaveCourse")
                        .param("courseId", "1")
                        .param("studentId", "1")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(studentService, times(1)).leaveCourse(1, 1);

        //cleanup
    }

}
