package com.bridgepay.payment_processor.messaging;

import com.bridgepay.payment_processor.model.dto.PaymentCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsPublisher {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${bridgepay.sqs.payment-events-queue-url}")
    private String queueUrl;

    public void publishPaymentCreatedEvent(PaymentCreatedEvent event) {
        try {
            log.info("Publishing PaymentCreatedEvent for paymentId={}", event.paymentId());
            String payload = objectMapper.writeValueAsString(event);
            sqsTemplate.send(queueUrl, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize PaymentCreatedEvent", e);
        }
    }
}
