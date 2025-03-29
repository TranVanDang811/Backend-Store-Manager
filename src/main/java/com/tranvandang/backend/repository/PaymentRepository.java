package com.tranvandang.backend.repository;

import com.tranvandang.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,String> {
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrderId(String orderId);
}
