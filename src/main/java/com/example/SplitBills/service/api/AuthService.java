package com.example.SplitBills.service.api;

import com.example.SplitBills.model.dto.request.RegisterRequest;
import com.example.SplitBills.model.dto.response.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    String register(RegisterRequest request);

    LoginResponse login(String email, String password);
}
