package com.example.SplitBills.service.impl;

import com.example.SplitBills.exception.IncorrectPasswordException;
import com.example.SplitBills.exception.UserAlreadyExistsException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.request.RegisterRequest;
import com.example.SplitBills.model.dto.response.LoginResponse;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.service.api.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceDefault implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public String register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new UserAlreadyExistsException(request.getUsername());
        }

        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException(request.getEmail());
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public LoginResponse login(String email, String password){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        String token = jwtUtils.generateToken(user.getSubId());
        Long expiration = jwtUtils.getExpirationMs();

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .expirationTime(expiration)
                .build();
    }
}