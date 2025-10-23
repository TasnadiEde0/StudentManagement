package org.learning.studentManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TestingWebApplicationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    GroupService groupService;
    @Autowired
    StudentService studentService;

    private MockMvc mockMvc;
    private Random random = new Random();

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void studentPage() throws Exception {
        mockMvc.perform(get("/student")).andExpect(status().isOk()).andExpect(view().name("student"));
    }

    @Test
    void groupPage() throws Exception {
        mockMvc.perform(get("/group")).andExpect(status().isOk()).andExpect(view().name("group"));
    }

//    @Test
//    void addGroup() throws Exception {
//        mockMvc.perform(post("/utils/group/add")
//                        .param("name", String.valueOf(random.nextInt(1000, 5000)))
//                )
////                .andDo(print())
//                .andExpect(status().is(302));
//    }
//
//    @Test
//    void alterGroup() throws Exception {
//
//        String name = String.valueOf(random.nextInt(1000, 5000));
//
//        mockMvc.perform(post("/utils/group/add")
//                        .param("name", name)
//                )
//                .andExpect(status().is(302));
//
//        Group group = groupService.findByName(name).orElse(null);
//        Integer groupId = group.getId();
//
//        String newName = String.valueOf(random.nextInt(1000, 5000));
//
//        mockMvc.perform(post("/utils/group/alter")
//                        .param("id", String.valueOf(groupId))
//                        .param("name", newName)
//                )
////                .andDo(print())
//                .andExpect(status().is(302));
//    }
//
//    @Test
//    void deleteGroup() throws Exception {
//        String name = String.valueOf(random.nextInt(1000, 5000));
//
//        mockMvc.perform(post("/utils/group/add")
//                        .param("name", name)
//                )
//                .andExpect(status().is(302));
//
//        Group group = groupService.findByName(name).orElse(null);
//        Integer groupId = group.getId();
//
//
//        mockMvc.perform(post("/utils/group/delete")
//                        .param("id", String.valueOf(groupId))
//                )
//                .andExpect(status().is(302));
//    }
//
//    @Test
//    void addStudent() throws Exception {
//        String firstName = String.valueOf(random.nextInt(1000, 5000));
//        String lastName = String.valueOf(random.nextInt(1000, 5000));
//        String email = String.valueOf(random.nextInt(1000, 5000));
//        email = email + "@gmail.com";
//        String cnp = String.valueOf(random.nextLong(1000000,9999999)) + String.valueOf(random.nextLong(100000,999999));
//        Group group = groupService.findAll().get(0);
//
//        mockMvc.perform(post("/utils/student/add")
//                        .param("firstName", firstName)
//                        .param("lastName", lastName)
//                        .param("email", email)
//                        .param("cnp", cnp)
//                        .param("groupid", String.valueOf(group.getId()) )
//                )
//                .andExpect(status().is(302));
//
//
//    }
//
//    @Test
//    void alterStudent() throws Exception {
//        String firstName = String.valueOf(random.nextInt(1000, 5000));
//        String lastName = String.valueOf(random.nextInt(1000, 5000));
//        String email = String.valueOf(random.nextInt(1000, 5000));
//        email = email + "@gmail.com";
//        String cnp = String.valueOf(random.nextLong(1000000,9999999)) + String.valueOf(random.nextLong(100000,999999));
//        Group group = groupService.findAll().get(0);
//
//        mockMvc.perform(post("/utils/student/add")
//                        .param("firstName", firstName)
//                        .param("lastName", lastName)
//                        .param("email", email)
//                        .param("cnp", cnp)
//                        .param("groupid", String.valueOf(group.getId()) )
//                )
//                .andExpect(status().is(302));
//
//    }

}