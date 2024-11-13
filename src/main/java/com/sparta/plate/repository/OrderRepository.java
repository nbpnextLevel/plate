package com.sparta.plate.repository;

import com.sparta.plate.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByOrderId(UUID orderId);
    Optional<Order> findByOrderIdAndIsDeletedFalse(UUID orderId);

    Optional<Order> findByOrderIdAndIsDeletedFalseAndIsCanceledFalse(UUID orderId);
    Optional<Order> findByUserIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalse(Long userId, UUID orderId);
    Optional<Order> findByStoreIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalse(UUID storeId, UUID orderId);

    Optional<Order> findByUserIdAndOrderIdAndIsDeletedFalse(Long userId, UUID orderId);
    Optional<Order> findByStoreIdAndOrderIdAndIsDeletedFalse(UUID storeId, UUID orderId);
}
