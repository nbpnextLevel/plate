package com.sparta.plate.repository;

import com.sparta.plate.entity.ProductSuggestionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductSuggestionRepository extends JpaRepository<ProductSuggestionRequest, UUID>, ProductSuggestionRepositoryCustom {
}

