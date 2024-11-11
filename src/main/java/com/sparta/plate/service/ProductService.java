package com.sparta.plate.service;

import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductHistory;
import com.sparta.plate.entity.ProductImage;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductHistoryRepository historyRepository;
    private final ProductImageRepository imageRepository;


    @Transactional
    public UUID createProduct(ProductRequestDto requestDto) {
        if (requestDto.getProductName() == null || requestDto.getProductName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }

        UUID productId = generateUniqueProductId();

        Product product = Product.toEntity(requestDto, productId);

        for (ProductImage productImage : product.getProductImageList()) {
            productImage.setProduct(product);
        }

        Product savedProduct = productRepository.save(product);

        return savedProduct.getId();
    }

    private UUID generateUniqueProductId() {
        UUID productId = UUID.randomUUID();
        while (productRepository.existsByProductId(productId)) {
            productId = UUID.randomUUID();
        }
        return productId;
    }

    @Transactional
    public void deleteProduct(UUID productId, Long userId) {
        Product product = productRepository.findById(productId).orElse(null);

        if (product != null) {
            product.markAsDeleted(userId);
            productRepository.save(product);
        }
    }

    @Transactional
    public void deleteProductHistory(UUID historyId, Long userId) {
        ProductHistory history = historyRepository.findById(historyId).orElse(null);

        if (history != null) {
            history.markAsDeleted(userId);
            historyRepository.save(history);
        }
    }

    @Transactional
    public void deleteProductImage(UUID imageId, Long userId) {
        ProductImage image = imageRepository.findById(imageId).orElse(null);

        if (image != null) {
            image.markAsDeleted(userId);
            imageRepository.save(image);
        }
    }
}
