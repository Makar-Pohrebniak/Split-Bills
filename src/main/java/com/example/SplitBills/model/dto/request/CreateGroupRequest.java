package com.example.SplitBills.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGroupRequest(
        @NotBlank(message = "Назва не може бути порожньою")
        @Size(max = 100)
        String name
) {}
