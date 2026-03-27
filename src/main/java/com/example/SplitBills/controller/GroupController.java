package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.request.CreateGroupRequest;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.service.api.GroupService;
import com.example.SplitBills.swagger.GroupControllerSwaggerDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController implements GroupControllerSwaggerDescription {

    private final GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<GroupEntity> createGroup(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserEntity owner
    ) {
        GroupEntity group = groupService.createGroup(request.name(), owner);
        return ResponseEntity.ok(group);
    }
    @GetMapping("/{id}")
    public ResponseEntity<GroupEntity> getGroupById(@PathVariable Long id) {
        return groupService.getGroupById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/my-groups")
    public ResponseEntity<List<GroupEntity>> getAllGroupsBySubId(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(groupService.getGroupsByUserSubId(user.getSubId()));
    }

}