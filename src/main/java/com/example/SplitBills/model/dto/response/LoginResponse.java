package com.example.SplitBills.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long expirationTime;
    private Long userId;
    private String username;
    private String email;
}
