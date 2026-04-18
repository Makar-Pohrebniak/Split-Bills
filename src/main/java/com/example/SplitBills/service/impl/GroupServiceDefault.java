package com.example.SplitBills.service.impl;

import com.example.SplitBills.enums.CurrencyEnum;
import com.example.SplitBills.enums.MemberAction;
import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.exception.NotYourGroupException;
import com.example.SplitBills.exception.TooManyRequestsException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.MemberEventDto;
import com.example.SplitBills.model.dto.response.GroupResponse;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.service.KafkaProducerService;
import com.example.SplitBills.service.api.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceDefault implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    private final Map<UUID, Long> creationLocks = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public GroupResponse createGroup(String groupName, CurrencyEnum currency, UUID subId) {
        long currentTime = System.currentTimeMillis();
        long lastRequestTime = creationLocks.getOrDefault(subId, 0L);

        if (currentTime - lastRequestTime < 3000) {
            throw new TooManyRequestsException();
        }

        creationLocks.put(subId, currentTime);

        UserEntity ownerEntity = userRepository.findBySubId(String.valueOf(subId))
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(subId)));

        GroupEntity group = new GroupEntity();
        group.setName(groupName);
        group.setOwner(subId);
        group.setCurrency(currency);
        group.getMembers().add(ownerEntity);

        GroupEntity savedGroup = groupRepository.save(group);

        kafkaProducerService.sendMemberEvent(new MemberEventDto(
                savedGroup.getId(),
                ownerEntity.getId(),
                MemberAction.ADDED
        ));

        return mapToResponse(savedGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long id) {
        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
        return mapToResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupsByUserSubId(UUID subId) {
        List<GroupEntity> groups = groupRepository.findAllByMembersSubId(subId);
        return groups.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteGroup(Long id, UUID ownerSubId) {
        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        if (!group.getOwner().equals(ownerSubId)) {
            throw new NotYourGroupException();
        }

        groupRepository.delete(group);
    }

    private GroupResponse mapToResponse(GroupEntity entity) {
        return GroupResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .owner(entity.getOwner())
                .currency(entity.getCurrency())
                .members(entity.getMembers().stream()
                        .map(user -> UserResponse.builder()
                                .subId(String.valueOf(user.getSubId()))
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}