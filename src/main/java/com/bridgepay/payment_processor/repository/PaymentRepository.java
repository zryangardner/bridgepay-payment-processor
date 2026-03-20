package com.bridgepay.payment_processor.repository;

import com.bridgepay.payment_processor.model.entity.Payment;
import com.bridgepay.payment_processor.model.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findBySenderId(String senderId);

    List<Payment> findByIsPrivateFalse();
}
