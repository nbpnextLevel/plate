package com.sparta.plate.repository;

import com.sparta.plate.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
    boolean existsByOrderProductId(UUID orderProductId);
}
