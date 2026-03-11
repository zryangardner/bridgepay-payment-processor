package com.bridgepay.payment_processor.service;

import com.bridgepay.payment_processor.model.dto.PaymentRequest;
import com.bridgepay.payment_processor.model.dto.PaymentResponse;
import com.bridgepay.payment_processor.model.entity.Payment;
import com.bridgepay.payment_processor.model.entity.PaymentStatus;
import com.bridgepay.payment_processor.exception.PaymentNotFoundException;
import com.bridgepay.payment_processor.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .senderId(request.getSenderId())
                .recipientId(request.getRecipientId())
                .description(request.getDescription())
                .status(PaymentStatus.PENDING)
                .build();

        return mapToResponse(paymentRepository.save(payment));
    }

    public PaymentResponse getPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return mapToResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<PaymentResponse> getPaymentsBySender(String senderId) {
        return paymentRepository.findBySenderId(senderId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PaymentResponse updatePaymentStatus(UUID id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        payment.setStatus(status);
        return mapToResponse(paymentRepository.save(payment));
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .senderId(payment.getSenderId())
                .recipientId(payment.getRecipientId())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
