package com.example.SplitBills.service.impl;

import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.service.api.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceDefault implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<UserResponse> getUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return Optional.of(UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build());
    }

    @Override
    public List<UserResponse> getUserByUsername(String username) {
        List<UserEntity> users = userRepository.findByUsername(username);

        return users.stream()
                .map(user -> UserResponse.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .toList();
    }

    @Override
    public Optional<UserResponse> getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return Optional.of(UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build());
    }
}
