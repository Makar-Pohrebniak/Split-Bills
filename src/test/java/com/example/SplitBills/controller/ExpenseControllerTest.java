package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.model.dto.request.UpdateExpenseDto;
import com.example.SplitBills.model.dto.response.ExpenseResponseDto;
import com.example.SplitBills.model.dto.response.PersonalBalanceResponseDto;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    private final Long EXPENSE_ID = 100L;
    private final String VALID_JSON = """
            {
                "amount": 100.00,
                "description": "Dinner",
                "categoryId": null,
                "shares": null
            }
            """;

    @Test
    @WithMockUser
    void addExpense_201() throws Exception {
        doNothing().when(expenseService).addExpense(eq(GROUP_ID), any(AddExpenseDto.class), any());

        mockMvc.perform(post("/api/v1/expenses/group/" + GROUP_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void addExpense_400() throws Exception {
        mockMvc.perform(post("/api/v1/expenses/group/" + GROUP_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addExpense_401() throws Exception {
        mockMvc.perform(post("/api/v1/expenses/group/" + GROUP_ID)
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

        mockMvc.perform(post("/api/v1/expenses/group/" + GROUP_ID)
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

        mockMvc.perform(post("/api/v1/expenses/group/" + GROUP_ID)
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

        mockMvc.perform(post("/api/v1/expenses/group/" + GROUP_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void getGroupExpenses_200() throws Exception {
        ExpenseResponseDto response = ExpenseResponseDto.builder()
                .id(EXPENSE_ID)
                .amount(BigDecimal.valueOf(100.00))
                .description("Dinner")
                .build();

        when(expenseService.getExpensesByGroupId(eq(GROUP_ID), any())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/expenses/group/" + GROUP_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EXPENSE_ID))
                .andExpect(jsonPath("$[0].amount").value(100.00));
    }

    @Test
    @WithMockUser
    void getExpense_200() throws Exception {
        ExpenseResponseDto response = ExpenseResponseDto.builder()
                .id(EXPENSE_ID)
                .amount(BigDecimal.valueOf(100.00))
                .description("Dinner")
                .build();

        when(expenseService.getExpenseById(eq(EXPENSE_ID), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/expenses/" + EXPENSE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXPENSE_ID));
    }

    @Test
    @WithMockUser
    void updateExpense_200() throws Exception {
        doNothing().when(expenseService).updateExpense(eq(EXPENSE_ID), any(UpdateExpenseDto.class), any());

        mockMvc.perform(put("/api/v1/expenses/" + EXPENSE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteExpense_204() throws Exception {
        doNothing().when(expenseService).deleteExpense(eq(EXPENSE_ID), any());

        mockMvc.perform(delete("/api/v1/expenses/" + EXPENSE_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getGroupExpenses_404_GroupNotFound() throws Exception {
        when(expenseService.getExpensesByGroupId(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        mockMvc.perform(get("/api/v1/expenses/group/" + GROUP_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getExpense_404_NotFound() throws Exception {
        when(expenseService.getExpenseById(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        mockMvc.perform(get("/api/v1/expenses/" + EXPENSE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateExpense_403_Forbidden() throws Exception {
        doThrow(new AccessDeniedException("Forbidden"))
                .when(expenseService).updateExpense(any(), any(), any());

        mockMvc.perform(put("/api/v1/expenses/" + EXPENSE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateExpense_404_NotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"))
                .when(expenseService).updateExpense(any(), any(), any());

        mockMvc.perform(put("/api/v1/expenses/" + EXPENSE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteExpense_403_Forbidden() throws Exception {
        doThrow(new AccessDeniedException("Forbidden"))
                .when(expenseService).deleteExpense(any(), any());

        mockMvc.perform(delete("/api/v1/expenses/" + EXPENSE_ID)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deleteExpense_404_NotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"))
                .when(expenseService).deleteExpense(any(), any());

        mockMvc.perform(delete("/api/v1/expenses/" + EXPENSE_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getUserBalance_200() throws Exception {
        PersonalBalanceResponseDto response = new PersonalBalanceResponseDto(
                BigDecimal.valueOf(500.00),
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(300.00)
        );

        when(expenseService.getUserBalanceInGroup(eq(GROUP_ID), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/expenses/group/" + GROUP_ID + "/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPaidByMe").value(500.00))
                .andExpect(jsonPath("$.totalMyShares").value(200.00))
                .andExpect(jsonPath("$.netBalance").value(300.00));
    }

    @Test
    @WithMockUser
    void getUserBalance_403() throws Exception {
        when(expenseService.getUserBalanceInGroup(any(), any()))
                .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/v1/expenses/group/" + GROUP_ID + "/balance"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getUserBalance_404() throws Exception {
        when(expenseService.getUserBalanceInGroup(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        mockMvc.perform(get("/api/v1/expenses/group/" + GROUP_ID + "/balance"))
                .andExpect(status().isNotFound());
    }
}