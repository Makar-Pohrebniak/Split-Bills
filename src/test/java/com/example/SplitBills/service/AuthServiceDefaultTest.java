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
import com.example.SplitBills.repository.RefreshTokenRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

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
    private RefreshTokenRepository refreshTokenRepository;

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

        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 3600000L);
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
        String rawPassword = "Password_pass1!";
        String email = "test@gmail.com";
        List<String> expectedRoles = List.of("USER");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(testUser.getSubId(), expectedRoles)).thenReturn("access.token");
        when(jwtUtils.generateRefreshToken(testUser.getSubId())).thenReturn("refresh.token");
        when(jwtUtils.getExpirationMs()).thenReturn(3600000L);

        LoginResponse response = authService.login(email, rawPassword);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals("access.token", response.getToken()),
                () -> assertEquals("refresh.token", response.getRefreshToken()),
                () -> assertEquals("Ihor", response.getUsername()),
                () -> assertEquals(1L, response.getUserId()),
                () -> assertEquals(email, response.getEmail())
        );

        verify(refreshTokenRepository).save(any());
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

    @Test
    void refresh_Success_ShouldReturnNewLoginResponse() {
        String oldRefreshToken = "old_refresh_token";
        com.example.SplitBills.security.RefreshTokenRedis storedToken = com.example.SplitBills.security.RefreshTokenRedis.builder()
                .token(oldRefreshToken)
                .subId(testUser.getSubId())
                .build();

        when(refreshTokenRepository.findById(oldRefreshToken)).thenReturn(Optional.of(storedToken));
        when(userRepository.findBySubId(testUser.getSubId().toString())).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateToken(any(), any())).thenReturn("new_access_token");
        when(jwtUtils.generateRefreshToken(any())).thenReturn("new_refresh_token");
        when(jwtUtils.getExpirationMs()).thenReturn(3600000L);

        LoginResponse response = authService.refresh(oldRefreshToken);

        assertNotNull(response);
        assertEquals("new_access_token", response.getToken());
        assertEquals("new_refresh_token", response.getRefreshToken());
        verify(refreshTokenRepository).deleteById(oldRefreshToken);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void refresh_InvalidToken_ShouldThrowException() {
        String invalidToken = "invalid_token";
        when(refreshTokenRepository.findById(invalidToken)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.refresh(invalidToken)
        );

        assertEquals("Invalid or expired refresh token", exception.getMessage());
        verify(refreshTokenRepository, never()).deleteById(anyString());
    }

    @Test
    void logout_ShouldDeleteTokenFromRepository() {
        String tokenToDelete = "token_to_delete";

        authService.logout(tokenToDelete);

        verify(refreshTokenRepository).deleteById(tokenToDelete);
    }

    @Test
    void register_EmailAlreadyExists_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("NewUser");
        request.setEmail("existing@gmail.com");

        when(userRepository.existsByUsername("NewUser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@gmail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_DefaultRoleNotFound_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("John");
        request.setEmail("john@gmail.com");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByRole(Role.USER)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.register(request)
        );

        assertTrue(exception.getMessage().contains("Default role USER not found"));
    }
}