package com.sparta.plate.repository;

import com.sparta.plate.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Modifying
    @Query("UPDATE Product p SET p.maxOrderLimit = :maxOrderLimit, p.stockQuantity = :stockQuantity WHERE p.id = :productId")
    void updateStockAndLimit(UUID productId, int maxOrderLimit, int stockQuantity);
}

