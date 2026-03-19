package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.request.LoginRequest;
import com.example.SplitBills.model.dto.request.RegisterRequest;
import com.example.SplitBills.model.dto.response.LoginResponse;
import com.example.SplitBills.service.api.AuthService;
import com.example.SplitBills.swagger.AuthControllerSwaggerDescription;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerSwaggerDescription {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
        String result = authService.register(request);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
