package org.learning.studentManagement;

import jakarta.validation.constraints.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupControllerTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private GroupService groupService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "user", password = "user")
    void groupPage_isOk() throws Exception {
        //setup
        when(groupService.findAll()).thenReturn(new ArrayList<>());

        //execution
        mockMvc.perform(get("/group"))
                .andExpect(status().isOk())
                .andExpect(view().name("group"));
        verify(groupService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
    void addGroup_isOk() throws Exception {
        //setup
        when(groupService.save("groupName")).thenReturn(new Group());

        //execution
        mockMvc.perform(post("/group/add")
                        .param("name", "groupName")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/group"))
                .andDo(print());
        verify(groupService, times(1)).save("groupName");

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
    void addGroup_invalidCharacters_is4xx() throws Exception {
        //setup
        String invalidGroupName = "$^*&*%^";
        when(groupService.save("$^*&*%^")).thenThrow(IllegalArgumentException.class);

        //execution
        mockMvc.perform(post("/group/add")
                        .param("name", invalidGroupName)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(groupService, times(1)).save("$^*&*%^");

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
    void deleteGroup_isOk() throws Exception {
        //setup
        doNothing().when(groupService).delete("1");

        //execution
        mockMvc.perform(post("/group/delete")
                        .param("id", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/group"))
                .andDo(print());
        verify(groupService, times(1)).delete("1");

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", roles={"USER", "ADMIN"})
    void deleteGroup_wrongId_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(groupService).delete("0");

        //execution
        mockMvc.perform(post("/group/delete")
                        .param("id", "0")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(groupService, times(1)).delete("0");

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", authorities={"ROLE_USER", "ROLE_ADMIN"})
    void alterGroup_isOk() throws Exception {
        //setup
        doNothing().when(groupService).update("1", "newGroupName");

        //execution
        mockMvc.perform(post("/group/alter")
                        .param("id", "1")
                        .param("name", "newGroupName")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/group"))
                .andDo(print());
        verify(groupService, times(1)).update("1", "newGroupName");

        //cleanup
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"USER", "ADMIN"})
    void alterGroup_duplicateNewName_is4xx() throws Exception {
        //setup
        doThrow(IllegalArgumentException.class).when(groupService).update("0", "groupName");

        //execution
        mockMvc.perform(post("/group/alter")
                        .param("id", "0")
                        .param("name", "groupName")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error"))
                .andDo(print());
        verify(groupService, times(1)).update("0", "groupName");

        //cleanup
    }

}
