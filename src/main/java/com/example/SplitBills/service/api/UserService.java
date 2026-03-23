package com.example.SplitBills.service.api;

import com.example.SplitBills.model.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserResponse> getUser(Long id);

    List<UserResponse> getUserByUsername(String username);

    Optional<UserResponse> getUserByEmail(String email);
}
