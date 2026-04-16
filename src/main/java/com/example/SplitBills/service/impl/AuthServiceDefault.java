package com.example.SplitBills.service.impl;

import com.example.SplitBills.enums.Role;
import com.example.SplitBills.exception.BadRefreshTokenException;
import com.example.SplitBills.exception.IncorrectPasswordException;
import com.example.SplitBills.exception.UserAlreadyExistsException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.request.RegisterRequest;
import com.example.SplitBills.model.dto.response.LoginResponse;
import com.example.SplitBills.model.entity.RoleEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.RefreshTokenRepository;
import com.example.SplitBills.repository.RoleRepository;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.security.RefreshTokenRedis;
import com.example.SplitBills.service.api.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceDefault implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshExpirationMs;

    @Override
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        RoleEntity userRole = roleRepository.findByRole(Role.USER)
                .orElseThrow(() -> new UserNotFoundException("Default role USER not found"));

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public LoginResponse login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRole().name())
                .toList();

        return createLoginResponse(user, roles);
    }

    @Override
    public LoginResponse refresh(String oldRefreshToken) {
        RefreshTokenRedis storedToken = refreshTokenRepository.findById(oldRefreshToken)
                .orElseThrow(BadRefreshTokenException::new);

        refreshTokenRepository.deleteById(oldRefreshToken);

        UserEntity user = userRepository.findBySubId(String.valueOf(storedToken.getSubId()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRole().name())
                .toList();

        return createLoginResponse(user, roles);
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    private LoginResponse createLoginResponse(UserEntity user, List<String> roles) {
        String accessToken = jwtUtils.generateToken(user.getSubId(), roles);
        String refreshToken = jwtUtils.generateRefreshToken(user.getSubId());

        RefreshTokenRedis redisToken = RefreshTokenRedis.builder()
                .token(refreshToken)
                .subId(user.getSubId())
                .ttl(refreshExpirationMs / 1000)
                .build();

        refreshTokenRepository.save(redisToken);

        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .expirationTime(jwtUtils.getExpirationMs())
                .build();
    }
}