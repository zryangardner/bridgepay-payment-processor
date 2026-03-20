package com.bridgepay.payment_processor.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @Builder.Default
    private String currency = "USD";

    @NotBlank
    private String senderId;

    @NotBlank
    private String recipientId;

    private String description;

    @Builder.Default
    private boolean isPrivate = false;
}
