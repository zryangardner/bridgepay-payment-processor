package com.bridgepay.payment_processor.messaging;

import com.bridgepay.payment_processor.model.dto.PaymentCreatedEvent;
import com.bridgepay.payment_processor.model.entity.PaymentStatus;
import com.bridgepay.payment_processor.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    @Test
    void onPaymentCreated_shouldUpdatePaymentStatusToProcessing() {
        UUID paymentId = UUID.randomUUID();
        PaymentCreatedEvent event = new PaymentCreatedEvent(
                paymentId.toString(),
                new BigDecimal("100.00"),
                "USD",
                "sender-1",
                "recipient-1"
        );

        paymentEventConsumer.onPaymentCreated(event);

        verify(paymentService).updatePaymentStatus(paymentId, PaymentStatus.PROCESSING);
    }
}
