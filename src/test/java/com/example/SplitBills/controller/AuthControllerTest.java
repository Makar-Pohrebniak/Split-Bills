package com.example.SplitBills.controller;

import com.example.SplitBills.exception.IncorrectPasswordException;
import com.example.SplitBills.exception.UserAlreadyExistsException;
import com.example.SplitBills.model.dto.request.LoginRequest;
import com.example.SplitBills.model.dto.request.RegisterRequest;
import com.example.SplitBills.model.dto.response.LoginResponse;
import com.example.SplitBills.service.api.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ObjectMapper.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userRegistration_returns201() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("John");
        registerRequest.setPassword("password");
        registerRequest.setEmail("john@gmail.com");

        when(authService.register(any(RegisterRequest.class))).thenReturn("User registered successfully");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                        .andExpect(status().isCreated())
                        .andExpect(content().string("User registered successfully"));
    }
    @Test
    void secondAttemptOfUserRegistration_returns409() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("John");
        registerRequest.setPassword("password");
        registerRequest.setEmail("john@gmail.com");

        when(authService.register(any(RegisterRequest.class))).thenThrow(new UserAlreadyExistsException(registerRequest.getEmail()));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("The user already exists with john@gmail.com"));
    }
    @Test
    void login_returns200() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@gmail.com");
        loginRequest.setPassword("password");

        LoginResponse loginResponse = LoginResponse.builder()
                .token("test-jwt-token")
                .type("Bearer")
                .expirationTime(3600000L)
                .userId(1L)
                .username("John")
                .email("john@gmail.com")
                .build();

        when(authService.login(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("John"));
    }

    @Test
    void loginWithWrongPassword_returns401() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@gmail.com");
        loginRequest.setPassword("wrong-pass");

        when(authService.login(any(), any())).thenThrow(new IncorrectPasswordException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorType").value("INCORRECT_PASSWORD"))
                .andExpect(jsonPath("$.errorMessage").value("Incorrect Password"));
    }
}
