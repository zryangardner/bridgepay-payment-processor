package com.bridgepay.payment_processor.service;

import com.bridgepay.payment_processor.exception.PaymentNotFoundException;
import com.bridgepay.payment_processor.model.dto.PaymentRequest;
import com.bridgepay.payment_processor.model.dto.PaymentResponse;
import com.bridgepay.payment_processor.model.entity.Payment;
import com.bridgepay.payment_processor.model.entity.PaymentStatus;
import com.bridgepay.payment_processor.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment buildPayment(UUID id, PaymentStatus status) {
        return Payment.builder()
                .id(id)
                .amount(new BigDecimal("100.00"))
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
    void createPayment_shouldReturnPendingPayment() {
        PaymentRequest request = PaymentRequest.builder()
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .senderId("sender-1")
                .recipientId("recipient-1")
                .description("Test payment")
                .build();

        UUID generatedId = UUID.randomUUID();
        Payment savedPayment = buildPayment(generatedId, PaymentStatus.PENDING);

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentResponse response = paymentService.createPayment(request);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getAmount()).isEqualByComparingTo("100.00");
        assertThat(response.getCurrency()).isEqualTo("USD");
        assertThat(response.getSenderId()).isEqualTo("sender-1");
        assertThat(response.getRecipientId()).isEqualTo("recipient-1");
        assertThat(response.getDescription()).isEqualTo("Test payment");
    }

    @Test
    void getPayment_shouldReturnPayment_whenExists() {
        UUID id = UUID.randomUUID();
        Payment payment = buildPayment(id, PaymentStatus.PENDING);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPayment(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getAmount()).isEqualByComparingTo("100.00");
        assertThat(response.getCurrency()).isEqualTo("USD");
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getSenderId()).isEqualTo("sender-1");
        assertThat(response.getRecipientId()).isEqualTo("recipient-1");
    }

    @Test
    void getPayment_shouldThrowPaymentNotFoundException_whenNotFound() {
        UUID id = UUID.randomUUID();

        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment(id))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void updatePaymentStatus_shouldUpdateAndReturnPayment() {
        UUID id = UUID.randomUUID();
        Payment existing = buildPayment(id, PaymentStatus.PENDING);
        Payment updated = buildPayment(id, PaymentStatus.COMPLETED);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(existing)).thenReturn(updated);

        PaymentResponse response = paymentService.updatePaymentStatus(id, PaymentStatus.COMPLETED);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.getId()).isEqualTo(id);
    }

    @Test
    void getPaymentsByStatus_shouldReturnFilteredList() {
        Payment p1 = buildPayment(UUID.randomUUID(), PaymentStatus.COMPLETED);
        Payment p2 = buildPayment(UUID.randomUUID(), PaymentStatus.COMPLETED);

        when(paymentRepository.findByStatus(PaymentStatus.COMPLETED)).thenReturn(List.of(p1, p2));

        List<PaymentResponse> responses = paymentService.getPaymentsByStatus(PaymentStatus.COMPLETED);

        assertThat(responses).hasSize(2);
        assertThat(responses).allMatch(r -> r.getStatus() == PaymentStatus.COMPLETED);
        assertThat(responses).extracting(PaymentResponse::getId)
                .containsExactlyInAnyOrder(p1.getId(), p2.getId());
    }
}
