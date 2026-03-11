package com.bridgepay.payment_processor.model.dto;

import com.bridgepay.payment_processor.model.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String senderId;
    private String recipientId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
