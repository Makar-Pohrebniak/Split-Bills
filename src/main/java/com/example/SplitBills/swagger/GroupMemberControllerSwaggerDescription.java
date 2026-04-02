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

import java.util.Set;
import java.util.UUID;

@Tag(name = "Group Members Management", description = "Endpoints for managing members within a group")
public interface GroupMemberControllerSwaggerDescription {

    @Operation(summary = "Get all members of a specific group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved members",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Access denied: you are not a member of this group",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Set<UserResponse>> getMembers(Long groupId, UUID requesterSubId);

    @Operation(summary = "Add a new member to the group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member successfully added"),
            @ApiResponse(responseCode = "403", description = "Only the group owner can add members",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group or User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> addMember(Long groupId, UUID friendId, UUID subId);

    @Operation(summary = "Remove a member from the group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member successfully removed"),
            @ApiResponse(responseCode = "403", description = "Only the group owner can remove members",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group or User not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> removeMember(Long groupId, UUID friendId, UUID subId);
}