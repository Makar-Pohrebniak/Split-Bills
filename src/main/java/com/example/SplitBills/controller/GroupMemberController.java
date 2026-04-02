package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.service.api.GroupMembersService;
import com.example.SplitBills.swagger.GroupMemberControllerSwaggerDescription;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/v1/groups/{groupId}/members")
public class GroupMemberController implements GroupMemberControllerSwaggerDescription {

    private final GroupMembersService groupMembersService;

    @GetMapping
    public ResponseEntity<Set<UserResponse>> getMembers(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UUID subId
    ) {
        return ResponseEntity.ok(groupMembersService.getMembers(groupId, subId));
    }

    @PostMapping("/add/{friendId}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long groupId,
            @PathVariable UUID friendId,
            @AuthenticationPrincipal UUID subId
    ) {
        groupMembersService.addMember(groupId, subId, friendId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable UUID friendId,
            @AuthenticationPrincipal UUID subId
    ) {
        groupMembersService.removeMember(groupId, subId, friendId);
        return ResponseEntity.noContent().build();
    }
}