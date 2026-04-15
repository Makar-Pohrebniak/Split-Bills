package com.example.SplitBills.model.dto.response;

import com.example.SplitBills.enums.CurrencyEnum;
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
    private CurrencyEnum currency;
    private UUID owner;
    private Set<UserResponse> members;
}
