package com.sparta.plate.service;

import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductImage;
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

    @Transactional
    // public ProductResponseDto createProduct(ProductRequestDto requestDto, Long createdBy) {
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        if (requestDto.getProductName() == null || requestDto.getProductName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }

        UUID productId = generateUniqueProductId();

        Product product = Product.toEntity(requestDto, 1L, productId);

        for (ProductImage productImage : product.getProductImageList()) {
            productImage.setProduct(product);
        }

        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.toDto(savedProduct);
    }

    private UUID generateUniqueProductId() {
        UUID productId = UUID.randomUUID();
        while (productRepository.existsByProductId(productId)) {
            productId = UUID.randomUUID(); // 중복되면 새 UUID 생성
        }
        return productId;
    }
}
