package org.learning.studentManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.glassfish.jaxb.core.v2.TODO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.model.dto.CourseDto;
import org.learning.studentManagement.model.dto.Mapper;
import org.learning.studentManagement.model.dto.StudentDto;
import org.learning.studentManagement.model.dto.StudentListingDto;
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

    @Autowired
    ObjectMapper  objectMapper;
    @Autowired
    Mapper mapper;

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
        when(studentService.count()).thenReturn(1);
        when(studentService.findAllFiltered(null, "id", 1)).thenReturn(new ArrayList<>(List.of(createMockStudent())));

        //execution
        mockMvc.perform(get("/student"))
                .andExpect(status().isOk()).andExpect(view().name("student"));
        verify(studentService, times(1)).count();
        verify(studentService, times(1)).findAllFiltered(null, "id", 1);

        //cleanup
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void studentPage_withFilters_isOk() throws Exception {
        //setup
        Group group = new Group();
        when(studentService.count()).thenReturn(1);
        when(groupService.findById(0)).thenReturn(group);
        when(studentService.findAllFiltered(group, "firstName", 1)).thenReturn(new ArrayList<>(List.of(createMockStudent())));

        //execution
        mockMvc.perform(get("/student?sortBy=firstName&pageNum=1&selectedGroup=0"))
                .andExpect(status().isOk()).andExpect(view().name("student"));
        verify(studentService, times(1)).count();
        when(studentService.findAllFiltered(group, "firstName", 1)).thenReturn(new ArrayList<>(List.of(createMockStudent())));

        //cleanup
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void fetchedStudent_withGroup_isOk() throws Exception {
        //setup
        Student student = createMockStudent();
        Group group = student.getGroup();
        StudentListingDto studentListingDto =
                new StudentListingDto(List.of(mapper.studentToStudentDto(student)), 1);

        when(groupService.findById(group.getId())).thenReturn(group);
        when(studentService.findAllFiltered(group, "firstName", 1)).thenReturn(List.of(student));
        when(studentService.count()).thenReturn(1);
        when(studentService.countByGroup(group)).thenReturn(1);

        //execution
        mockMvc.perform(get("/fetchedStudent?sortBy=firstName&pageNum=1&selectedGroup=" + group.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studentListingDto)));

        verify(studentService, times(1)).count();
        verify(studentService, times(1)).findAllFiltered(group, "firstName", 1);
        verify(studentService, times(1)).countByGroup(group);
        verify(studentService, times(1)).count();
        verify(groupService, times(1)).findById(1);

        //cleanup

    }

    @Test
    void fetchedStudent_withoutGroup_isOk() throws Exception {
        //setup
        Student student = createMockStudent();
        Group group = student.getGroup();
        StudentListingDto studentListingDto =
                new StudentListingDto(List.of(mapper.studentToStudentDto(student)), 1);

        when(studentService.findAllFiltered(null, "firstName", 1)).thenReturn(List.of(student));
        when(studentService.count()).thenReturn(1);

        //execution
        mockMvc.perform(get("/fetchedStudent?sortBy=firstName&pageNum=1&selectedGroup="))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studentListingDto)));

        verify(studentService, times(1)).count();
        verify(studentService, times(1)).findAllFiltered(null, "firstName", 1);
        verify(studentService, never()).countByGroup(group);
        verify(studentService, times(1)).count();
        verify(groupService, never()).findById(1);

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
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
    void addStudent_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        StudentDto studentDto = new StudentDto(null, "firstName", "lastName", "1234512345123",
                "email@email.email", null, "groupName", null, file);
        when(studentService.save(studentDto)).thenReturn(new Student());

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
        verify(studentService, times(1)).save(studentDto);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
    void addStudent_fileMissing_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(studentService).save(any());

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
        verify(studentService, times(1)).save(any());

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
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
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
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
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
    void alterStudent_isOk() throws Exception {
        //setup
        MockMultipartFile file = new MockMultipartFile("profilePic", "red.png",
                "multipart/form-data", "upload/imgs/red.png".getBytes());
        StudentDto studentDto = new StudentDto("1", "firstName", "lastName",
                "1234512345123", "email@email.email", null, null, "groupId", file);
        doNothing().when(studentService).update(studentDto);

        //execution
        mockMvc.perform(multipart("/student/alter")
                        .file(file)
                        .param("id", "1")
                        .param("firstName", "firstName")
                        .param("lastName", "lastName")
                        .param("email", "email@email.email")
                        .param("cnp", "1234512345123")
                        .param("groupId", "groupId")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"))
                .andDo(print());
        verify(studentService, times(1)).update(studentDto);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
    void alterStudent_duplicateCnp_is4xxx() throws Exception {
        //setup
        StudentDto studentDto = new StudentDto("1", null, null,
                "1234512345123", null, null, null, null, null);

        doThrow(IllegalArgumentException.class).when(studentService).update(studentDto);

        //execution
        mockMvc.perform(multipart("/student/alter")
                        .param("id", "1")
                        .param("cnp", "1234512345123")
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
        verify(studentService, times(1)).update(studentDto);

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
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
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
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
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"}) //TODO
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
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
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
