package com.example.SplitBills.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private UUID owner;
    private Set<UserResponse> members;
}
