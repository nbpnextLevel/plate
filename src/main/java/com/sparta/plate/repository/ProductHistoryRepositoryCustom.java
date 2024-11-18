package com.sparta.plate.repository;

import com.sparta.plate.dto.request.ProductHistoryQueryDto;
import com.sparta.plate.dto.response.ProductHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductHistoryRepositoryCustom {
    Page<ProductHistoryResponseDto> searchAll(Pageable pageable, ProductHistoryQueryDto queryDto);
}

