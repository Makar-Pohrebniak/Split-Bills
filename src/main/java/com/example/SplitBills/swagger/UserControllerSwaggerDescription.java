package com.example.SplitBills.swagger;

import com.example.SplitBills.exception.ApiError;
import com.example.SplitBills.model.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Tag(name = "User Management", description = "Endpoints for user profile operations and search")
public interface UserControllerSwaggerDescription {

    @Operation(summary = "Get user by ID", description = "Returns user data by their primary Long ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User with provided ID does not exist",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    Optional<UserResponse> getUser(@PathVariable Long id);

    @Operation(summary = "Get user by username", description = "Retrieves user information using their unique username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    List<UserResponse> getUserByUsername(@PathVariable String username);

    @Operation(summary = "Get user by email", description = "Retrieves user information using their unique email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format provided",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "User with provided email not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    Optional<UserResponse> getUserByEmail(@PathVariable String email);
}