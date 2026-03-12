package com.bridgepay.payment_processor.messaging;

import com.bridgepay.payment_processor.model.dto.PaymentCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqsPublisherTest {

    @Mock
    private SqsTemplate sqsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SqsPublisher sqsPublisher;

    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/000000000000/test-queue";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sqsPublisher, "queueUrl", QUEUE_URL);
    }

    @Test
    void publishPaymentCreatedEvent_shouldSerializeAndSendToSqsQueue() throws JsonProcessingException {
        PaymentCreatedEvent event = new PaymentCreatedEvent(
                "payment-id-123",
                new BigDecimal("100.00"),
                "USD",
                "sender-1",
                "recipient-1"
        );
        String serialized = "{\"paymentId\":\"payment-id-123\"}";

        when(objectMapper.writeValueAsString(event)).thenReturn(serialized);

        sqsPublisher.publishPaymentCreatedEvent(event);

        verify(sqsTemplate).send(QUEUE_URL, serialized);
    }

    @Test
    void publishPaymentCreatedEvent_shouldThrowRuntimeException_whenSerializationFails() throws JsonProcessingException {
        PaymentCreatedEvent event = new PaymentCreatedEvent(
                "payment-id-123",
                new BigDecimal("100.00"),
                "USD",
                "sender-1",
                "recipient-1"
        );

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("error") {});

        assertThatThrownBy(() -> sqsPublisher.publishPaymentCreatedEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to serialize PaymentCreatedEvent");
    }
}
