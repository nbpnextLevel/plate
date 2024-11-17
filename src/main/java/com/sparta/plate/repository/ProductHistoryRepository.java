package com.sparta.plate.repository;

import com.sparta.plate.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, UUID>, ProductHistoryRepositoryCustom {
    @Query(value = "SELECT * FROM p_product_history ph WHERE ph.product_id = :productId AND ph.is_deleted = false ORDER BY ph.created_at DESC LIMIT 1", nativeQuery = true)
    ProductHistory findLatestByProductId(UUID productId);

    boolean existsByProductId(UUID productId);


}

