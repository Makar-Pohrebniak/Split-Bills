package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.request.CreateGroupRequest;
import com.example.SplitBills.model.dto.response.GroupResponse;
import com.example.SplitBills.service.api.GroupService;
import com.example.SplitBills.swagger.GroupControllerSwaggerDescription;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/v1/groups")
public class GroupController implements GroupControllerSwaggerDescription {

    private final GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<GroupResponse> createGroup(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UUID subId
    ) {
        GroupResponse group = groupService.createGroup(request.name(), subId);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping("/my-groups")
    public ResponseEntity<List<GroupResponse>> getAllGroupsBySubId(
            @AuthenticationPrincipal UUID subId
    ) {
        return ResponseEntity.ok(groupService.getGroupsByUserSubId(subId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UUID subId
    ) {
        groupService.deleteGroup(id, subId);
        return ResponseEntity.noContent().build();
    }
}