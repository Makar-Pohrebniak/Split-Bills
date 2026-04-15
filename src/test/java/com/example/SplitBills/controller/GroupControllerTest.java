package com.example.SplitBills.controller;

import com.example.SplitBills.GlobalExceptionHandler;
import com.example.SplitBills.enums.CurrencyEnum;
import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.exception.NotYourGroupException;
import com.example.SplitBills.model.dto.request.CreateGroupRequest;
import com.example.SplitBills.model.dto.response.GroupResponse;
import com.example.SplitBills.service.api.GroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private GroupResponse testResponse;
    private final UUID subId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        testResponse = GroupResponse.builder()
                .id(1L)
                .name("Test Group")
                .owner(subId)
                .currency(CurrencyEnum.UAH)
                .members(Set.of())
                .build();
    }

    @Test
    void createGroup_Returns200() throws Exception {
        CreateGroupRequest request = new CreateGroupRequest("Test Group", CurrencyEnum.UAH);

        when(groupService.createGroup(eq("Test Group"), eq(CurrencyEnum.UAH), any()))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/groups/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Group"))
                .andExpect(jsonPath("$.currency").value("UAH"));
    }

    @Test
    void getGroupById_Returns200() throws Exception {
        when(groupService.getGroupById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getGroupById_Returns404() throws Exception {
        when(groupService.getGroupById(99L)).thenThrow(new GroupNotFoundException(99L));

        mockMvc.perform(get("/api/v1/groups/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllGroupsBySubId_Returns200() throws Exception {
        when(groupService.getGroupsByUserSubId(any())).thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/groups/my-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Group"));
    }

    @Test
    void deleteGroup_Returns204() throws Exception {
        doNothing().when(groupService).deleteGroup(eq(1L), any());

        mockMvc.perform(delete("/api/v1/groups/1"))
                .andExpect(status().isNoContent());

        verify(groupService, times(1)).deleteGroup(eq(1L), any());
    }

    @Test
    void deleteGroup_Returns400_WhenNotOwner() throws Exception {
        doThrow(new NotYourGroupException()).when(groupService).deleteGroup(eq(1L), any());

        mockMvc.perform(delete("/api/v1/groups/1"))
                .andExpect(status().isBadRequest());
    }
}