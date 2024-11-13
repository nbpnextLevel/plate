package com.sparta.plate.repository;

import com.sparta.plate.dto.request.ProductImageQueryDto;
import com.sparta.plate.dto.response.ProductImageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductImageRepositoryCustom {
    Page<ProductImageResponseDto> searchAll(Pageable pageable, ProductImageQueryDto queryDto);
}

