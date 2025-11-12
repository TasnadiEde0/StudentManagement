package org.learning.studentManagement;

import jakarta.servlet.Filter;
import lombok.Cleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.studentManagement.configs.SecurityConfigs;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.service.GroupService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Security;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityControllerTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private GroupService groupService;

    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        Mockito.reset(groupService);
    }

    @Test
    public void styles_isOk() throws Exception {
        //setup

        //execute
        mockMvc.perform(get("/styles.css"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/css"));

        //cleanup

    }

    @Test
    public void login_isOk()  throws Exception {
        //setup

        //execute
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "admin")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/student"));

        //cleanup

    }

    @Test
    public void login_incorrectCredentials_unauthorized() throws Exception {
        //setup

        //execute
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "notPassword")
                        .with(csrf())
                )
                .andExpect(status().is4xxClientError())
                .andExpect(unauthenticated())
                .andExpect(view().name("error"));

        //cleanup

    }

    @Test
    public void groupPage_isUnauthenticated()  throws Exception {
        //setup
        when(groupService.findAll()).thenReturn(List.of(new Group()));

        //execute
        mockMvc.perform(get("/group")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(groupService, never()).save("groupName");

        //cleanup

    }

    @Test
    public void addGroup_isUnauthenticatedAndUnauthorized()  throws Exception {
        //setup

        //execute
        mockMvc.perform(post("/group/add")
                .param("name", "groupName")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(groupService, never()).save("groupName");

        //cleanup

    }



}
