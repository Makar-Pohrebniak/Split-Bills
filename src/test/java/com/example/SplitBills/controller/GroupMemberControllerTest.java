package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.service.api.GroupMembersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupMemberController.class)
class GroupMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupMembersService groupMembersService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private final String MY_SUB_ID_STR = "550e8400-e29b-41d4-a716-446655440000";
    private final Long groupId = 1L;
    private final UUID friendId = UUID.randomUUID();

    @Test
    @WithMockUser(username = MY_SUB_ID_STR)
    void getMembers_Success() throws Exception {
        UserResponse response = UserResponse.builder()
                .subId(friendId.toString())
                .username("JohnDoe")
                .email("john@example.com")
                .build();

        when(groupMembersService.getMembers(eq(groupId), any()))
                .thenReturn(Set.of(response));

        mockMvc.perform(get("/api/v1/groups/{groupId}/members", groupId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("JohnDoe"))
                .andExpect(jsonPath("$[0].subId").value(friendId.toString()));
    }

    @Test
    @WithMockUser(username = MY_SUB_ID_STR)
    void addMember_Success() throws Exception {
        mockMvc.perform(post("/api/v1/groups/{groupId}/members/add/{friendId}", groupId, friendId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(groupMembersService).addMember(eq(groupId), any(), eq(friendId));
    }

    @Test
    @WithMockUser(username = MY_SUB_ID_STR)
    void removeMember_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/groups/{groupId}/members/remove/{friendId}", groupId, friendId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(groupMembersService).removeMember(eq(groupId), any(), eq(friendId));
    }
}