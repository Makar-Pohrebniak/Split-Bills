package com.example.SplitBills.service;

import com.example.SplitBills.enums.CurrencyEnum;
import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.exception.NotYourGroupException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.response.GroupResponse;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.service.impl.GroupServiceDefault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceDefaultTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private GroupServiceDefault groupService;

    private UUID testSubId;
    private UserEntity testUser;
    private GroupEntity testGroup;

    @BeforeEach
    void setUp() {
        TransactionSynchronizationManager.initSynchronization();

        testSubId = UUID.randomUUID();

        testUser = new UserEntity();
        testUser.setUsername("testUser");
        testUser.setEmail("test@gmail.com");
        testUser.setSubId(testSubId);

        testGroup = new GroupEntity();
        testGroup.setId(1L);
        testGroup.setName("Test Group");
        testGroup.setOwner(testSubId);
        testGroup.setCurrency(CurrencyEnum.UAH);
        testGroup.setMembers(new HashSet<>(List.of(testUser)));
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clear();
        }
    }

    @Test
    void createGroup_Success() {
        when(userRepository.findBySubId(anyString())).thenReturn(Optional.of(testUser));
        when(groupRepository.save(any(GroupEntity.class))).thenReturn(testGroup);

        GroupResponse response = groupService.createGroup("Test Group", CurrencyEnum.UAH, testSubId);

        TransactionSynchronizationManager.getSynchronizations()
                .forEach(sync -> sync.afterCommit());

        assertNotNull(response);
        assertEquals("Test Group", response.getName());
        assertEquals(testSubId, response.getOwner());

        verify(groupRepository, times(1)).save(any(GroupEntity.class));
        verify(kafkaProducerService).sendMemberEvent(any());
    }

    @Test
    void createGroup_UserNotFound_ThrowsException() {
        when(userRepository.findBySubId(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                groupService.createGroup("Test Group", CurrencyEnum.UAH, testSubId)
        );
    }

    @Test
    void getGroupById_Success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        GroupResponse response = groupService.getGroupById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getGroupById_NotFound_ThrowsException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.getGroupById(1L));
    }

    @Test
    void getGroupsByUserSubId_Success() {
        when(groupRepository.findAllByMembersSubId(testSubId)).thenReturn(List.of(testGroup));

        List<GroupResponse> responses = groupService.getGroupsByUserSubId(testSubId);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void deleteGroup_Success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        assertDoesNotThrow(() -> groupService.deleteGroup(1L, testSubId));

        verify(groupRepository, times(1)).delete(testGroup);
    }

    @Test
    void deleteGroup_NotOwner_ThrowsException() {
        UUID strangerId = UUID.randomUUID();
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        assertThrows(NotYourGroupException.class, () ->
                groupService.deleteGroup(1L, strangerId)
        );

        verify(groupRepository, never()).delete(any());
    }
}