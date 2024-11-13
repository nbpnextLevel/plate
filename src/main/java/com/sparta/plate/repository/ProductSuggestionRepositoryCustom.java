package com.sparta.plate.repository;

import com.sparta.plate.dto.request.ProductSuggestionQueryDto;
import com.sparta.plate.dto.response.ProductSuggestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSuggestionRepositoryCustom {
    Page<ProductSuggestionResponseDto> searchAll(Pageable pageable, ProductSuggestionQueryDto queryDto);
}

