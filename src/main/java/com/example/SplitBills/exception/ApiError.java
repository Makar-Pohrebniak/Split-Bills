package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ApiError {

    @Schema(example = "2026-03-18T12:00:00Z")
    private final Instant dateTime;

    private final ErrorType errorType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String errorMessage;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, Object> additionalInfo = new HashMap<>();
}
