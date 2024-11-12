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

    Optional<Payment> findByOrder(Order order);
}
