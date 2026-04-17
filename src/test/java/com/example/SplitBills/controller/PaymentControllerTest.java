package com.example.SplitBills.controller;

import com.example.SplitBills.enums.PaymentStatus;
import com.example.SplitBills.exception.PaymentNotFoundException;
import com.example.SplitBills.exception.InvalidPaymentOperationException;
import com.example.SplitBills.exception.UnauthorizedAccessException;
import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.model.dto.response.PaymentResponseDto;
import com.example.SplitBills.security.JwtUtils;
import com.example.SplitBills.service.api.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private final Long GROUP_ID = 1L;
    private final Long PAYMENT_ID = 50L;

    @Test
    @WithMockUser
    void createPayment_201() throws Exception {
        PaymentResponseDto response = PaymentResponseDto.builder()
                .id(PAYMENT_ID)
                .amount(BigDecimal.valueOf(150.00))
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentService.createPayment(any(UUID.class), eq(GROUP_ID), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/payments/group/" + GROUP_ID)
                        .with(csrf())
                        .param("comment", "Settling debt"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void createPayment_400_NoDebt() throws Exception {
        when(paymentService.createPayment(any(), any(), any()))
                .thenThrow(new InvalidPaymentOperationException("No debt found"));

        mockMvc.perform(post("/api/v1/payments/group/" + GROUP_ID).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createPayment_403_NotMember() throws Exception {
        when(paymentService.createPayment(any(), any(), any()))
                .thenThrow(new UnauthorizedAccessException("Not a member"));

        mockMvc.perform(post("/api/v1/payments/group/" + GROUP_ID).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void createPayment_404_GroupMissing() throws Exception {
        when(paymentService.createPayment(any(), any(), any()))
                .thenThrow(new GroupNotFoundException(GROUP_ID));

        mockMvc.perform(post("/api/v1/payments/group/" + GROUP_ID).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void approvePayment_200() throws Exception {
        PaymentResponseDto response = PaymentResponseDto.builder().id(PAYMENT_ID).status(PaymentStatus.CONFIRMED).build();
        when(paymentService.approvePayment(eq(PAYMENT_ID), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/payments/" + PAYMENT_ID + "/approve").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void approvePayment_400_AlreadyProcessed() throws Exception {
        when(paymentService.approvePayment(any(), any()))
                .thenThrow(new InvalidPaymentOperationException("Already CONFIRMED"));

        mockMvc.perform(patch("/api/v1/payments/" + PAYMENT_ID + "/approve").with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void approvePayment_404_NotFound() throws Exception {
        when(paymentService.approvePayment(any(), any()))
                .thenThrow(new PaymentNotFoundException(PAYMENT_ID));

        mockMvc.perform(patch("/api/v1/payments/" + PAYMENT_ID + "/approve").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void declinePayment_200() throws Exception {
        PaymentResponseDto response = PaymentResponseDto.builder().id(PAYMENT_ID).status(PaymentStatus.REJECTED).build();
        when(paymentService.declinePayment(eq(PAYMENT_ID), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/payments/" + PAYMENT_ID + "/decline").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void declinePayment_403_NotOwner() throws Exception {
        when(paymentService.declinePayment(any(), any()))
                .thenThrow(new UnauthorizedAccessException("Only admin can decline"));

        mockMvc.perform(patch("/api/v1/payments/" + PAYMENT_ID + "/decline").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getGroupPayments_200() throws Exception {
        when(paymentService.getGroupPayments(GROUP_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/payments/group/" + GROUP_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getGroupPayments_404_GroupNotFound() throws Exception {
        when(paymentService.getGroupPayments(GROUP_ID)).thenThrow(new GroupNotFoundException(GROUP_ID));

        mockMvc.perform(get("/api/v1/payments/group/" + GROUP_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPayment_401_NoToken() throws Exception {
        mockMvc.perform(post("/api/v1/payments/group/" + GROUP_ID).with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}