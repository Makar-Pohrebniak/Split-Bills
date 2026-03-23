package com.example.SplitBills.service;

import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.service.impl.UserServiceDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceDefaultTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceDefault userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .username("Ihor")
                .email("test@gmail.com")
                .password("encoded_password")
                .build();
    }

    @Test
    void getUser_IdExists_ShouldReturnUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<UserResponse> result = userService.getUser(1L);

        assertTrue(result.isPresent());
        assertEquals("Ihor", result.get().getUsername());
        assertEquals("test@gmail.com", result.get().getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUser_IdDoesNotExist_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(99L));
        verify(userRepository).findById(99L);
    }

    @Test
    void getUserByUsername_Exists_ShouldReturnListWithUser() {
        when(userRepository.findByUsername("Ihor")).thenReturn(List.of(testUser));

        List<UserResponse> result = userService.getUserByUsername("Ihor");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Ihor", result.get(0).getUsername());
        verify(userRepository).findByUsername("Ihor");
    }

    @Test
    void getUserByUsername_DoesNotExist_ShouldReturnEmptyList() {
        when(userRepository.findByUsername("Unknown")).thenReturn(List.of());

        List<UserResponse> result = userService.getUserByUsername("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserByEmail_Exists_ShouldReturnUserResponse() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(testUser));

        Optional<UserResponse> result = userService.getUserByEmail("test@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("test@gmail.com", result.get().getEmail());
        verify(userRepository).findByEmail("test@gmail.com");
    }

    @Test
    void getUserByEmail_DoesNotExist_ShouldThrowUserNotFoundException() {
        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("notfound@gmail.com"));
    }
}