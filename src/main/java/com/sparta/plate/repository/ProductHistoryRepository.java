package com.sparta.plate.repository;

import com.sparta.plate.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, UUID> {

    boolean existsByProductId(UUID productId);
}

