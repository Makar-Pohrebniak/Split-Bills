package com.example.SplitBills.model.dto.request;

import com.example.SplitBills.enums.CurrencyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGroupRequest(
        @NotBlank(message = "Required name")
        @Size(max = 100)
        String name,

        @NotNull(message = "Add currency")
        CurrencyEnum currency

) {}
