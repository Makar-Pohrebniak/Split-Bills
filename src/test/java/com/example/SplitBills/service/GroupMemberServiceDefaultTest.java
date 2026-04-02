package com.example.SplitBills.service.impl;

import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.exception.NotYourGroupException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMembersServiceDefaultTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupMembersServiceDefault groupMembersService;

    private final Long groupId = 1L;
    private final UUID ownerSubId = UUID.randomUUID();
    private final UUID friendId = UUID.randomUUID();
    private GroupEntity group;
    private UserEntity friend;

    @BeforeEach
    void setUp() {
        group = new GroupEntity();
        group.setId(groupId);
        group.setOwner(ownerSubId);
        group.setMembers(new HashSet<>());

        friend = new UserEntity();
        friend.setSubId(friendId);
        friend.setUsername("Friend");
    }

    @Test
    void addMember_Success() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findBySubId(String.valueOf(friendId))).thenReturn(Optional.of(friend));

        groupMembersService.addMember(groupId, ownerSubId, friendId);

        assertTrue(group.getMembers().contains(friend));
        verify(groupRepository).findById(groupId);
        verify(userRepository).findBySubId(String.valueOf(friendId));
    }

    @Test
    void addMember_ThrowsNotYourGroupException() {
        UUID strangerId = UUID.randomUUID();
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        assertThrows(NotYourGroupException.class, () ->
                groupMembersService.addMember(groupId, strangerId, friendId));
    }

    @Test
    void removeMember_Success() {
        group.getMembers().add(friend);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findBySubId(String.valueOf(friendId))).thenReturn(Optional.of(friend));

        groupMembersService.removeMember(groupId, ownerSubId, friendId);

        assertFalse(group.getMembers().contains(friend));
    }

    @Test
    void getMembers_Success() {
        group.getMembers().add(friend);
        UserEntity owner = new UserEntity();
        owner.setSubId(ownerSubId);
        group.getMembers().add(owner);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        Set<UserResponse> result = groupMembersService.getMembers(groupId, ownerSubId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getMembers_ThrowsNotYourGroupException_WhenUserNotMember() {
        UUID strangerId = UUID.randomUUID();
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        assertThrows(NotYourGroupException.class, () ->
                groupMembersService.getMembers(groupId, strangerId));
    }

    @Test
    void addMember_ThrowsGroupNotFound() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () ->
                groupMembersService.addMember(groupId, ownerSubId, friendId));
    }

    @Test
    void addMember_ThrowsUserNotFound() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findBySubId(String.valueOf(friendId))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                groupMembersService.addMember(groupId, ownerSubId, friendId));
    }
}