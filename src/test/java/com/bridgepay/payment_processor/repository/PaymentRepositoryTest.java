package com.bridgepay.payment_processor.repository;

import com.bridgepay.payment_processor.model.entity.Payment;
import com.bridgepay.payment_processor.model.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Payment buildPayment(String senderId, String recipientId, PaymentStatus status) {
        return buildPayment(senderId, recipientId, status, false);
    }

    private Payment buildPayment(String senderId, String recipientId, PaymentStatus status, boolean isPrivate) {
        return Payment.builder()
                .amount(new BigDecimal("75.00"))
                .currency("USD")
                .status(status)
                .senderId(senderId)
                .recipientId(recipientId)
                .description("Test payment")
                .isPrivate(isPrivate)
                .build();
    }

    @Test
    void save_shouldPersistPayment() {
        Payment payment = buildPayment("sender-1", "recipient-1", PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);
        entityManager.flush();
        entityManager.clear();

        Optional<Payment> found = paymentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getSenderId()).isEqualTo("sender-1");
        assertThat(found.get().getRecipientId()).isEqualTo("recipient-1");
        assertThat(found.get().getAmount()).isEqualByComparingTo("75.00");
        assertThat(found.get().getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void findByStatus_shouldReturnMatchingPayments() {
        entityManager.persistAndFlush(buildPayment("sender-1", "recipient-1", PaymentStatus.PENDING));
        entityManager.persistAndFlush(buildPayment("sender-2", "recipient-2", PaymentStatus.PENDING));
        entityManager.persistAndFlush(buildPayment("sender-3", "recipient-3", PaymentStatus.COMPLETED));

        List<Payment> pending = paymentRepository.findByStatus(PaymentStatus.PENDING);
        List<Payment> completed = paymentRepository.findByStatus(PaymentStatus.COMPLETED);

        assertThat(pending).hasSize(2);
        assertThat(pending).allMatch(p -> p.getStatus() == PaymentStatus.PENDING);
        assertThat(completed).hasSize(1);
        assertThat(completed.getFirst().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void findBySenderId_shouldReturnMatchingPayments() {
        entityManager.persistAndFlush(buildPayment("alice", "recipient-1", PaymentStatus.PENDING));
        entityManager.persistAndFlush(buildPayment("alice", "recipient-2", PaymentStatus.COMPLETED));
        entityManager.persistAndFlush(buildPayment("bob", "recipient-3", PaymentStatus.PENDING));

        List<Payment> alicePayments = paymentRepository.findBySenderId("alice");
        List<Payment> bobPayments = paymentRepository.findBySenderId("bob");

        assertThat(alicePayments).hasSize(2);
        assertThat(alicePayments).allMatch(p -> p.getSenderId().equals("alice"));
        assertThat(bobPayments).hasSize(1);
        assertThat(bobPayments.getFirst().getSenderId()).isEqualTo("bob");
    }

    @Test
    void findByIsPrivateFalse_shouldReturnOnlyPublicPayments() {
        entityManager.persistAndFlush(buildPayment("sender-1", "recipient-1", PaymentStatus.PENDING, false));
        entityManager.persistAndFlush(buildPayment("sender-2", "recipient-2", PaymentStatus.PENDING, false));
        entityManager.persistAndFlush(buildPayment("sender-3", "recipient-3", PaymentStatus.PENDING, true));

        List<Payment> publicPayments = paymentRepository.findByIsPrivateFalse();

        assertThat(publicPayments).hasSize(2);
        assertThat(publicPayments).allMatch(p -> !p.isPrivate());
    }

    @Test
    void save_shouldAutoGenerateUUID() {
        Payment payment = buildPayment("sender-1", "recipient-1", PaymentStatus.PENDING);
        assertThat(payment.getId()).isNull();

        Payment saved = entityManager.persistAndFlush(payment);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_shouldSetTimestamps() {
        Payment payment = buildPayment("sender-1", "recipient-1", PaymentStatus.PENDING);
        assertThat(payment.getCreatedAt()).isNull();
        assertThat(payment.getUpdatedAt()).isNull();

        Payment saved = entityManager.persistAndFlush(payment);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
