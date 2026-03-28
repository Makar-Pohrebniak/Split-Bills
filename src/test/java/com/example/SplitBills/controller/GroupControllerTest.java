package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.request.CreateGroupRequest;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.service.api.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private UserEntity user;
    private GroupEntity group;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setSubId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        group = new GroupEntity();
        group.setId(1L);
        group.setName("Test Group");
        group.setOwner(user);
        group.setMembers(new HashSet<>(List.of(user)));
    }

    @Test
    void createGroup_returns200() throws Exception {
        when(groupService.createGroup(any(), any())).thenReturn(group);

        mockMvc.perform(post("/api/v1/groups/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Group\"}")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Group"));
    }

    @Test
    void getGroupById_returns200() throws Exception {
        when(groupService.getGroupById(anyLong())).thenReturn(group);

        mockMvc.perform(get("/api/v1/groups/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Group"));
    }

    @Test
    void getAllGroupsBySubId_returns200() throws Exception {
        when(groupService.getGroupsByUserSubId(any())).thenReturn(List.of(group));

        mockMvc.perform(get("/api/v1/groups/my-groups")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Group"));
    }
}