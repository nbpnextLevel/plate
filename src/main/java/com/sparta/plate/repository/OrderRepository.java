package com.sparta.plate.repository;

import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findById(UUID orderId);
}
