package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.service.api.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpenseController.class)
@AutoConfigureMockMvc
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private final Long GROUP_ID = 1L;
    private final String VALID_JSON = """
            {
                "amount": 100.00,
                "description": "Dinner"
            }
            """;

    @Test
    @WithMockUser
    void addExpense_201() throws Exception {
        doNothing().when(expenseService).addExpense(eq(GROUP_ID), any(AddExpenseDto.class), any());

        mockMvc.perform(post("/api/v1/groups/" + GROUP_ID + "/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void addExpense_400() throws Exception {
        mockMvc.perform(post("/api/v1/groups/" + GROUP_ID + "/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addExpense_401() throws Exception {
        mockMvc.perform(post("/api/v1/groups/" + GROUP_ID + "/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void addExpense_403() throws Exception {
        doThrow(new AccessDeniedException("Forbidden"))
                .when(expenseService).addExpense(any(), any(), any());

        mockMvc.perform(post("/api/v1/groups/" + GROUP_ID + "/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void addExpense_404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"))
                .when(expenseService).addExpense(any(), any(), any());

        mockMvc.perform(post("/api/v1/groups/" + GROUP_ID + "/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void addExpense_500() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error"))
                .when(expenseService).addExpense(any(), any(), any());

        mockMvc.perform(post("/api/v1/groups/" + GROUP_ID + "/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isInternalServerError());
    }
}