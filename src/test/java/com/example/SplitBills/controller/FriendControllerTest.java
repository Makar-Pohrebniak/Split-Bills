package com.example.SplitBills.controller;

import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.service.api.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FriendController.class)
@AutoConfigureMockMvc
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private UserResponse friendResponse;
    private final String FRIEND_ID = "f984a3b5-2056-4f6a-8e75-274011db5daf";

    @BeforeEach
    void setUp() {
        friendResponse = UserResponse.builder()
                .username("Makar")
                .email("makar@gmail.com")
                .build();
    }

    @Test
    @WithMockUser
    void addFriend_200() throws Exception {
        doNothing().when(friendService).addFriend(FRIEND_ID);

        mockMvc.perform(post("/api/v1/friends/" + FRIEND_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void addFriend_404() throws Exception {
        doThrow(new UserNotFoundException(FRIEND_ID)).when(friendService).addFriend(FRIEND_ID);

        mockMvc.perform(post("/api/v1/friends/" + FRIEND_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("The user not found with: " + FRIEND_ID));
    }

    @Test
    void addFriend_401() throws Exception {
        mockMvc.perform(post("/api/v1/friends/" + FRIEND_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getFriends_200() throws Exception {
        when(friendService.getFriends()).thenReturn(List.of(friendResponse));

        mockMvc.perform(get("/api/v1/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Makar"));
    }

    @Test
    void getFriends_401() throws Exception {
        mockMvc.perform(get("/api/v1/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void deleteFriend_200() throws Exception {
        doNothing().when(friendService).removeFriend(FRIEND_ID);

        mockMvc.perform(delete("/api/v1/friends/" + FRIEND_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteFriend_404() throws Exception {
        doThrow(new UserNotFoundException(FRIEND_ID)).when(friendService).removeFriend(FRIEND_ID);

        mockMvc.perform(delete("/api/v1/friends/" + FRIEND_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFriend_401() throws Exception {
        mockMvc.perform(delete("/api/v1/friends/" + FRIEND_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}