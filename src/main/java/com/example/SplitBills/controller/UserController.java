package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.service.api.UserService;
import com.example.SplitBills.swagger.UserControllerSwaggerDescription;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserController implements UserControllerSwaggerDescription {

    private final UserService userService;

    @GetMapping("/{id}")
    public Optional<UserResponse> getUser(@PathVariable Long id) {
    return userService.getUser(id);
    }

    @GetMapping("/username/{username}")
    public List<UserResponse> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/email/{email}")
    public Optional<UserResponse> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

}
