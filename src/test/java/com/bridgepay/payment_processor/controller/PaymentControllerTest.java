package com.bridgepay.payment_processor.controller;

import com.bridgepay.payment_processor.exception.PaymentNotFoundException;
import com.bridgepay.payment_processor.model.dto.PaymentRequest;
import com.bridgepay.payment_processor.model.dto.PaymentResponse;
import com.bridgepay.payment_processor.model.entity.PaymentStatus;
import com.bridgepay.payment_processor.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    private PaymentResponse buildResponse(UUID id, PaymentStatus status) {
        return PaymentResponse.builder()
                .id(id)
                .amount(new BigDecimal("250.00"))
                .currency("USD")
                .status(status)
                .senderId("sender-1")
                .recipientId("recipient-1")
                .description("Test payment")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createPayment_shouldReturn201_whenValidRequest() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .amount(new BigDecimal("250.00"))
                .currency("USD")
                .senderId("sender-1")
                .recipientId("recipient-1")
                .description("Test payment")
                .build();

        UUID id = UUID.randomUUID();
        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(buildResponse(id, PaymentStatus.PENDING));

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.senderId").value("sender-1"))
                .andExpect(jsonPath("$.recipientId").value("recipient-1"));
    }

    @Test
    void createPayment_shouldReturn400_whenInvalidRequest() throws Exception {
        // missing senderId (blank) and amount (null)
        PaymentRequest request = PaymentRequest.builder()
                .recipientId("recipient-1")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.senderId").exists())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void getPayment_shouldReturn200_whenExists() throws Exception {
        UUID id = UUID.randomUUID();
        when(paymentService.getPayment(id)).thenReturn(buildResponse(id, PaymentStatus.PENDING));

        mockMvc.perform(get("/api/v1/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.senderId").value("sender-1"));
    }

    @Test
    void getPayment_shouldReturn404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(paymentService.getPayment(id)).thenThrow(new PaymentNotFoundException(id));

        mockMvc.perform(get("/api/v1/payments/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment not found with id: " + id));
    }

    @Test
    void updatePaymentStatus_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        when(paymentService.updatePaymentStatus(eq(id), eq(PaymentStatus.COMPLETED)))
                .thenReturn(buildResponse(id, PaymentStatus.COMPLETED));

        mockMvc.perform(patch("/api/v1/payments/{id}/status", id)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
