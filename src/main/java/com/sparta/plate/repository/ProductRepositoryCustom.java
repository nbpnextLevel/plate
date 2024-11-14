package com.sparta.plate.repository;

import com.sparta.plate.dto.request.ProductQueryDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductResponseDto> searchAll(Pageable pageable, ProductQueryDto queryDto, String role, Long userId);
}

