package com.example.SplitBills.service;

import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.service.impl.GroupServiceDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceDefaultTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupServiceDefault groupService;

    @Test
    void createGroup_shouldSaveAndAddOwner() {
        UserEntity owner = new UserEntity();
        owner.setId(1L);

        when(groupRepository.save(any(GroupEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GroupEntity result = groupService.createGroup("Test Group", owner);

        assertNotNull(result);
        assertEquals("Test Group", result.getName());
        assertEquals(owner, result.getOwner());
        assertTrue(result.getMembers().contains(owner));

        verify(groupRepository).save(any(GroupEntity.class));
    }

    @Test
    void getGroupById_shouldReturnGroup() {
        GroupEntity group = new GroupEntity();
        group.setId(1L);

        when(groupRepository.getGroupById(1L))
                .thenReturn(Optional.of(group));

        GroupEntity result = groupService.getGroupById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(groupRepository).getGroupById(1L);
    }

    @Test
    void getGroupById_shouldThrowException_whenNotFound() {
        when(groupRepository.getGroupById(1L))
                .thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () ->
                groupService.getGroupById(1L)
        );

        verify(groupRepository).getGroupById(1L);
    }

    @Test
    void getGroupsByUserSubId_shouldReturnGroups() {
        UUID subId = UUID.randomUUID();

        List<GroupEntity> groups = List.of(
                new GroupEntity(),
                new GroupEntity()
        );

        when(groupRepository.findAllByMemberSubId(subId))
                .thenReturn(groups);

        List<GroupEntity> result = groupService.getGroupsByUserSubId(subId.toString());

        assertEquals(2, result.size());
        verify(groupRepository).findAllByMemberSubId(subId);
    }

    @Test
    void getGroupsByUserSubId_shouldReturnEmptyList() {
        UUID subId = UUID.randomUUID();

        when(groupRepository.findAllByMemberSubId(subId))
                .thenReturn(List.of());

        List<GroupEntity> result = groupService.getGroupsByUserSubId(subId.toString());

        assertTrue(result.isEmpty());
        verify(groupRepository).findAllByMemberSubId(subId);
    }
}