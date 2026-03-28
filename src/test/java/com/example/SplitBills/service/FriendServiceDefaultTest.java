package com.example.SplitBills.service;

import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.exception.YouAreNotYourFriendException;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.service.impl.FriendServiceDefault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceDefaultTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendServiceDefault friendService;

    private UserEntity me;
    private UserEntity friend;

    private final UUID MY_UUID = UUID.randomUUID();
    private final UUID FRIEND_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        me = UserEntity.builder()
                .subId(MY_UUID)
                .username("Me")
                .friends(new HashSet<>())
                .build();

        friend = UserEntity.builder()
                .subId(FRIEND_UUID)
                .username("Makar")
                .friends(new HashSet<>())
                .build();

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.getName()).thenReturn(MY_UUID.toString());
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addFriend_Success() {
        when(userRepository.findBySubId(MY_UUID.toString())).thenReturn(Optional.of(me));
        when(userRepository.findBySubId(FRIEND_UUID.toString())).thenReturn(Optional.of(friend));

        friendService.addFriend(FRIEND_UUID.toString());

        assertTrue(me.getFriends().contains(friend));
    }

    @Test
    void addFriend_Self_ThrowsException() {
        when(userRepository.findBySubId(MY_UUID.toString())).thenReturn(Optional.of(me));

        assertThrows(YouAreNotYourFriendException.class, () -> friendService.addFriend(MY_UUID.toString()));
    }

    @Test
    void getFriends_ReturnsList() {
        me.getFriends().add(friend);
        when(userRepository.findBySubId(MY_UUID.toString())).thenReturn(Optional.of(me));

        List<UserResponse> result = friendService.getFriends();

        assertEquals(1, result.size());
        assertEquals("Makar", result.get(0).getUsername());
    }

    @Test
    void removeFriend_Success() {
        me.getFriends().add(friend);
        when(userRepository.findBySubId(MY_UUID.toString())).thenReturn(Optional.of(me));
        when(userRepository.findBySubId(FRIEND_UUID.toString())).thenReturn(Optional.of(friend));

        friendService.removeFriend(FRIEND_UUID.toString());

        assertFalse(me.getFriends().contains(friend));
    }
}