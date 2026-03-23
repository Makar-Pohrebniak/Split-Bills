package com.example.SplitBills.controller;

import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
                .username("Ihor")
                .email("test@gmail.com")
                .build();
    }

    @Test
    void getUserById_returns200() throws Exception {
        when(userService.getUser(1L)).thenReturn(Optional.of(userResponse));

        mockMvc.perform(get("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ihor"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    void getUserById_NotFound_returns404() throws Exception {
        when(userService.getUser(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/v1/users/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("The user not found with: 99"));
    }

    @Test
    void getUserByUsername_returns200() throws Exception {
        when(userService.getUserByUsername("Ihor")).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/v1/users/username/Ihor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Ihor"))
                .andExpect(jsonPath("$[0].email").value(userResponse.getEmail()));
    }

    @Test
    void getUserByEmail_returns200() throws Exception {
        when(userService.getUserByEmail("test@gmail.com")).thenReturn(Optional.of(userResponse));

        mockMvc.perform(get("/api/v1/users/email/test@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    void getUserByEmail_NotFound_returns404() throws Exception {
        String email = "notfound@gmail.com";
        when(userService.getUserByEmail(email)).thenThrow(new UserNotFoundException(email));

        mockMvc.perform(get("/api/v1/users/email/" + email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("The user not found with: notfound@gmail.com"));
    }
}