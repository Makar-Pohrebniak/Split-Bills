package com.example.SplitBills.service.impl;

import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.exception.NotYourGroupException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.service.api.GroupMembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMembersServiceDefault implements GroupMembersService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addMember(Long groupId, UUID ownerSubId, UUID friendId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!group.getOwner().equals(ownerSubId)) {
            throw new NotYourGroupException();
        }

        UserEntity user = userRepository.findBySubId(String.valueOf(friendId))
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(friendId)));

        group.getMembers().add(user);
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, UUID ownerSubId, UUID friendId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!group.getOwner().equals(ownerSubId)) {
            throw new NotYourGroupException();
        }

        UserEntity user = userRepository.findBySubId(String.valueOf(friendId))
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(friendId)));

        group.getMembers().remove(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UserResponse> getMembers(Long groupId, UUID requesterSubId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        boolean isMember = group.getMembers().stream()
                .anyMatch(user -> user.getSubId().equals(requesterSubId));

        if (!isMember) {
            throw new NotYourGroupException();
        }

        return group.getMembers().stream()
                .map(user -> UserResponse.builder()
                        .subId(String.valueOf(user.getSubId()))
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toSet());
    }

}
