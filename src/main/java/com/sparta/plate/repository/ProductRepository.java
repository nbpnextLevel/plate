package com.sparta.plate.repository;

import com.sparta.plate.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, ProductRepositoryCustom {
    @Query("SELECT s.user.id FROM Product p " +
            "JOIN p.store s " +
            "WHERE p.id = :productId")
    Long findUserIdByProductId(@Param("productId") UUID productId);
}

