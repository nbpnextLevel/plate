package com.sparta.plate.repository;

import com.sparta.plate.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, UUID>, ProductHistoryRepositoryCustom {
    @Query("SELECT ph FROM ProductHistory ph " +
            "WHERE ph.productId = :productId AND ph.isDeleted = false " +
            "ORDER BY ph.createdAt DESC")
    ProductHistory findLatestByProductId(UUID productId);

    boolean existsByProductId(UUID productId);
}

