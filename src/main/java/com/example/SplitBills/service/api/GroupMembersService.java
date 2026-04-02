package com.example.SplitBills.service.api;

import com.example.SplitBills.model.dto.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public interface GroupMembersService {

    void addMember(Long groupId, UUID ownerSubId, UUID memberId);

    void removeMember(Long groupId, UUID ownerSubId, UUID memberId);

    Set<UserResponse> getMembers(Long groupId, UUID requesterSubId);
}
