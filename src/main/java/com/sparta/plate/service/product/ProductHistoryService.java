package com.sparta.plate.service.product;

import com.sparta.plate.dto.request.ProductDetailsRequestDto;
import com.sparta.plate.dto.request.ProductHistoryQueryDto;
import com.sparta.plate.dto.response.ProductHistoryResponseDto;
import com.sparta.plate.entity.ProductHistory;
import com.sparta.plate.exception.ProductHistoryNotFoundException;
import com.sparta.plate.repository.ProductHistoryRepository;
import com.sparta.plate.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductHistoryService {

    private final ProductHistoryRepository historyRepository;

    @Transactional
    public void deleteProductHistory(UUID historyId, Long userId) {
        ProductHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new ProductHistoryNotFoundException("Product history not found with ID: " + historyId));

        history.markAsDeleted(userId);
        historyRepository.save(history);
    }

    public void createProductHistory(ProductDetailsRequestDto requestDto, UUID productId) {
        ProductHistory history = ProductHistory.toEntity(requestDto, productId);
        historyRepository.save(history);
    }

    public Page<ProductHistoryResponseDto> getProductHistories(ProductHistoryQueryDto requestDto) {
        Pageable pageable = PageableUtil.createPageable(requestDto.getPageNumber(), requestDto.getPageSize());
        return historyRepository.searchAll(pageable, requestDto);
    }
}
