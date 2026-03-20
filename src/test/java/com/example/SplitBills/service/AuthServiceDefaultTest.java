package com.example.SplitBills.service;

import com.example.SplitBills.enums.ErrorType;
import com.example.SplitBills.enums.Role;
import com.example.SplitBills.exception.IncorrectPasswordException;
import com.example.SplitBills.exception.UserAlreadyExistsException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.request.RegisterRequest;
import com.example.SplitBills.model.dto.response.LoginResponse;
import com.example.SplitBills.model.entity.RoleEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.RoleRepository;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.service.impl.AuthServiceDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceDefaultTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthServiceDefault authService;

    private UserEntity testUser;
    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        userRole = new RoleEntity();
        userRole.setRole(Role.USER);

        testUser = UserEntity.builder()
                .id(1L)
                .subId(UUID.randomUUID())
                .username("Ihor")
                .email("test@gmail.com")
                .password("encoded_password")
                .roles(Set.of(userRole))
                .build();
    }

    @Test
    void register_Success_ShouldReturnSuccessMessage() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("John");
        request.setEmail("john@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("John")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(roleRepository.findByRole(Role.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        String result = authService.register(request);

        assertEquals("User registered successfully", result);
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("John") &&
                        user.getEmail().equals("john@gmail.com") &&
                        user.getPassword().equals("encoded_password") &&
                        user.getRoles().contains(userRole)
        ));
    }

    @Test
    void register_UserAlreadyExists_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ExistingUser");
        request.setEmail("existing@gmail.com");

        when(userRepository.existsByUsername("ExistingUser")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                authService.register(request)
        );

        assertEquals(ErrorType.USER_ALREADY_EXISTS, exception.getErrorType());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void login_Success_ShouldReturnLoginResponse() {
        String rawPassword = "password123";
        String email = "test@gmail.com";
        List<String> expectedRoles = List.of("USER");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(testUser.getSubId(), expectedRoles)).thenReturn("valid.jwt.token");
        when(jwtUtils.getExpirationMs()).thenReturn(3600000L);

        LoginResponse response = authService.login(email, rawPassword);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals("valid.jwt.token", response.getToken()),
                () -> assertEquals("Ihor", response.getUsername()),
                () -> assertEquals(1L, response.getUserId()),
                () -> assertEquals(email, response.getEmail()),
                () -> assertEquals(3600000L, response.getExpirationTime())
        );

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, "encoded_password");
        verify(jwtUtils).generateToken(testUser.getSubId(), expectedRoles);
    }

    @Test
    void login_UserNotFound_ShouldThrowUserNotFoundException() {
        String email = "Unknown@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                authService.login(email, "any_pass")
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WrongPassword_ShouldThrowIncorrectPasswordException() {
        String email = "test@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong_pass", testUser.getPassword())).thenReturn(false);

        assertThrows(IncorrectPasswordException.class, () ->
                authService.login(email, "wrong_pass")
        );

        verify(jwtUtils, never()).generateToken(any(), any());
    }
}