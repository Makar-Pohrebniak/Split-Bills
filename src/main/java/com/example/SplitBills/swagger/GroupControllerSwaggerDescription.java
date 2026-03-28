package com.example.SplitBills.swagger;

import com.example.SplitBills.exception.ApiError;
import com.example.SplitBills.model.dto.request.CreateGroupRequest;
import com.example.SplitBills.model.dto.response.GroupResponse;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Group Management", description = "Endpoints for managing groups")
public interface GroupControllerSwaggerDescription {

    @Operation(summary = "Create a new group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Group successfully created",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid group name or missing request body",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<GroupResponse> createGroup(CreateGroupRequest request, UUID subId);

    @Operation(summary = "Get group by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group found",
                    content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<GroupResponse> getGroupById(Long id);

    @Operation(summary = "Get all groups of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved groups",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<GroupResponse>> getAllGroupsBySubId(UUID subId);

    @Operation(summary = "Delete group by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Group successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not an owner",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> deleteGroup(Long id, UUID subId);
}