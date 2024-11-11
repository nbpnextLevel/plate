package com.sparta.plate.service.product;

import com.sparta.plate.entity.ProductHistory;
import com.sparta.plate.exception.ProductHistoryNotFoundException;
import com.sparta.plate.repository.ProductHistoryRepository;
import com.sparta.plate.repository.ProductImageRepository;
import com.sparta.plate.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductHistoryService {

    private final ProductRepository productRepository;
    private final ProductHistoryRepository historyRepository;
    private final ProductImageRepository imageRepository;

    @Transactional
    public void deleteProductHistory(UUID historyId, Long userId) {
        ProductHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new ProductHistoryNotFoundException("Product history not found with ID: " + historyId)); // 예외 처리 추가

        history.markAsDeleted(userId);
        historyRepository.save(history);
    }

}