package com.example.SplitBills.swagger;

import com.example.SplitBills.exception.ApiError;
import com.example.SplitBills.model.dto.request.CreateGroupRequest;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Tag(name = "Group Management", description = "Endpoints for managing groups (creating, listing, retrieving by ID)")
public interface GroupControllerSwaggerDescription {

    @Operation(summary = "Create a new group", description = "Creates a new group with the specified name for the authenticated user. The creator is automatically added as a member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Group successfully created",
                    content = @Content(schema = @Schema(implementation = GroupEntity.class))),
            @ApiResponse(responseCode = "400", description = "Invalid group name or missing request body",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<GroupEntity> createGroup(CreateGroupRequest request, UserEntity owner);

    @Operation(summary = "Get group by ID", description = "Retrieves a group by its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group found",
                    content = @Content(schema = @Schema(implementation = GroupEntity.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<GroupEntity> getGroupById(Long id);

    @Operation(summary = "Get all groups of the authenticated user", description = "Retrieves all groups where the authenticated user is a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved groups",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupEntity.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<GroupEntity>> getAllGroupsBySubId(String subId);
}