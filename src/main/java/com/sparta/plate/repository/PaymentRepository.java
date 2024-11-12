package com.sparta.plate.repository;

import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findById(UUID paymentId);

    Optional<Payment> findByPaymentId(UUID paymentId);

    // userId로 결제 조회
    // User-Order-Payment, 주문이 사용자와 결제 사이에 중간 역할을 하기 때문에
    // Order로 조회를 해야함
    Optional<Payment> findByOrder(Order order);
}
