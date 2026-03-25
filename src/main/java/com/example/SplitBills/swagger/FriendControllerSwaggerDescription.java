package com.example.SplitBills.swagger;

import com.example.SplitBills.exception.ApiError;
import com.example.SplitBills.model.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Friend Management", description = "Endpoints for managing user social connections (adding, removing, and listing friends)")
public interface FriendControllerSwaggerDescription {

    @Operation(summary = "Add a friend", description = "Creates a friendship connection between the authenticated user and another user by their sub_id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend successfully added"),
            @ApiResponse(responseCode = "404", description = "Either current user or friend not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> addFriend(@PathVariable String friend_id);

    @Operation(summary = "Get friends list", description = "Retrieves a list of all users who are currently in the authenticated user's friend list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved friends list",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Full authentication is required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<UserResponse>> getFriends();

    @Operation(summary = "Delete a friend", description = "Removes a specific user from the authenticated user's friend list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend successfully removed"),
            @ApiResponse(responseCode = "404", description = "Friend connection or user not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Full authentication is required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> deleteFriend(@PathVariable String friend_id);
}