package com.bridgepay.payment_processor.messaging;

import com.bridgepay.payment_processor.model.dto.PaymentCreatedEvent;
import com.bridgepay.payment_processor.model.entity.PaymentStatus;
import com.bridgepay.payment_processor.service.PaymentService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    @SqsListener("${bridgepay.sqs.payment-events-queue-url}")
    public void onPaymentCreated(PaymentCreatedEvent event) {
        log.info("Received PaymentCreatedEvent for paymentId={}", event.paymentId());
        paymentService.updatePaymentStatus(UUID.fromString(event.paymentId()), PaymentStatus.PROCESSING);
        log.info("Payment {} status updated to PROCESSING", event.paymentId());
    }
}
