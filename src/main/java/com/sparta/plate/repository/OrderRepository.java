package com.sparta.plate.repository;

import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // 주문 조회시 사용
    Optional<Order> findByOrderIdAndIsDeletedFalseAndIsCanceledFalse(UUID orderId);
    Optional<Order> findByUserIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalse(Long userId, UUID orderId);
    Optional<Order> findByStoreIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalse(UUID storeId, UUID orderId);

    // 주문 취소 시 사용
    Optional<Order> findByUserIdAndOrderIdAndIsDeletedFalse(Long userId, UUID orderId);
    Optional<Order> findByStoreIdAndOrderIdAndIsDeletedFalse(UUID storeId, UUID orderId);
    Optional<Order> findByOrderIdAndIsDeletedFalse(UUID orderId);

    // 주문 수정 시 사용
    Optional<Order> findByUserIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(Long userId, UUID orderId, OrderStatusEnum orderStatus);
    Optional<Order> findByStoreIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(UUID storeId, UUID orderId, OrderStatusEnum orderStatus);
    Optional<Order> findByOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(UUID orderId, OrderStatusEnum orderStatus);


    // 주문 다건 조회 시 사용
    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.orderProductList " +
            "WHERE (:orderStatus IS NULL OR o.orderStatus = :orderStatus) " +
            "AND o.user.id = :userId "+
            "AND o.isDeleted = false",
            countQuery = "SELECT COUNT(o) FROM Order o " +
                    "WHERE (:orderStatus IS NULL OR o.orderStatus = :orderStatus) " +
                    "AND o.user.id = :userId "+
                    "AND o.isDeleted = false")
    Page<Order> findByUserOrdersWithProductList(Long userId, OrderStatusEnum orderStatus, Pageable pageable);

    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.orderProductList " +
            "WHERE (:orderStatus IS NULL OR o.orderStatus = :orderStatus) " +
            "AND o.store.id = :storeId "+
            "AND o.isDeleted = false",
            countQuery = "SELECT COUNT(o) FROM Order o " +
                    "WHERE (:orderStatus IS NULL OR o.orderStatus = :orderStatus) " +
                    "AND o.store.id = :storeId "+
                    "AND o.isDeleted = false")
    Page<Order> findByStoreOrdersWithProductList(UUID storeId, OrderStatusEnum orderStatus, Pageable pageable);

    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.orderProductList " +
            "WHERE (:orderStatus IS NULL OR o.orderStatus = :orderStatus) " +
            "AND o.isDeleted = false",
            countQuery = "SELECT COUNT(o) FROM Order o "+
                    "WHERE (:orderStatus IS NULL OR o.orderStatus = :orderStatus) " +
                    "AND o.isDeleted = false")
    Page<Order> findByOrdersWithProductList(OrderStatusEnum orderStatus, Pageable pageable);
}
