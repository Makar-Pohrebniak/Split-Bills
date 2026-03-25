package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.service.api.FriendService;
import com.example.SplitBills.swagger.FriendControllerSwaggerDescription;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class FriendController implements FriendControllerSwaggerDescription {

    private final FriendService friendService;

    @PostMapping("/{friend_id}")
    public ResponseEntity<Void> addFriend(@PathVariable String friend_id) {
        friendService.addFriend(friend_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getFriends() {
        return ResponseEntity.ok(friendService.getFriends());
    }

    @DeleteMapping("/{friend_id}")
    public ResponseEntity<Void> deleteFriend(@PathVariable String friend_id) {
        friendService.removeFriend(friend_id);
        return ResponseEntity.ok().build();
    }
}
