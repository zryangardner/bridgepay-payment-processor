package com.bridgepay.payment_processor.model.dto;

import java.math.BigDecimal;

public record PaymentCreatedEvent(
        String paymentId,
        BigDecimal amount,
        String currency,
        String senderId,
        String recipientId
) {}
