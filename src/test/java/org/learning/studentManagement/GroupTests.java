package org.learning.studentManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GroupService groupService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void groupPage_isOk() throws Exception {
        //execution
        mockMvc.perform(get("/group"))
                .andExpect(status().isOk())
                .andExpect(view().name("group"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addGroup_isOk() throws Exception {
        //execution
        mockMvc.perform(post("/group/add")
                        .param("name", "groupName")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/group"))
                .andDo(print());

        //cleanup
        groupService.findByName("groupName")
                .ifPresent(group -> groupService.delete(String.valueOf(group.getId())));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void addGroup_invalidCharacters_is4xx() throws Exception {
        //setup
        String invalidGroupName = "$^*&*%^";

        //execution
        mockMvc.perform(post("/group/add")
                        .param("name", invalidGroupName)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());

    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteGroup_isOk() throws Exception {
        //setup
        Group group = groupService.save("groupName");
        String groupId = String.valueOf(group.getId());

        //execution
        mockMvc.perform(post("/group/delete")
                        .param("id", groupId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/group"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void deleteGroup_wrongId_is4xx() throws Exception {
        //setup
//        List<Group> groups = groupService.findAll();
//        Group group = groups.get(groups.size() - 1);
//        String incorrectGroupId = String.valueOf(group.getId() + 1);

        Group group =  groupService.save("groupName");
        String incorrectGroupId = String.valueOf(group.getId()) + "0";

        //execution
        mockMvc.perform(post("/group/delete")
                        .param("id", incorrectGroupId)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());

        //cleanup
        groupService.delete(String.valueOf(group.getId()));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterGroup_isOk() throws Exception {
        //setup
        Group group = groupService.save("groupName");
        String groupId = String.valueOf(group.getId());

        //execution
        mockMvc.perform(post("/group/alter")
                        .param("id", groupId)
                        .param("name", "newGroupName")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/group"))
                .andDo(print());

        //cleanup
        groupService.delete(groupId);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void alterGroup_duplicateNewName_is4xx() throws Exception {
        //setup
        Group group = groupService.save("groupName");
        String groupId = String.valueOf(group.getId());
        Group otherGroup = groupService.save("otherGroupName");

        //execution
        mockMvc.perform(post("/group/alter")
                        .param("id", groupId)
                        .param("name", otherGroup.getName())
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());

        //cleanup
        groupService.delete(groupId);
        groupService.delete(String.valueOf(otherGroup.getId()));
    }

}
