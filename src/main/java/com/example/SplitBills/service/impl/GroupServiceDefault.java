package com.example.SplitBills.service.impl;

import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.service.api.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupServiceDefault implements GroupService {

    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public GroupEntity createGroup(String name, UserEntity owner) {
        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setOwner(owner);
        group.getMembers().add(owner);

        return groupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GroupEntity> getGroupById(Long id) {
        return groupRepository.getGroupById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupEntity> getGroupsByUserSubId(UUID subId) {
        return groupRepository.findAllByMemberSubId(subId);
    }
}