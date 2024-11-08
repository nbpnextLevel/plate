package com.sparta.plate.service;

import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import com.sparta.plate.entity.Product;
import com.sparta.plate.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto, Long createdBy) {
        if (requestDto.getProductName() == null || requestDto.getProductName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }

        Product product = new Product();
        product = product.toEntity(requestDto, createdBy);
        log.info("product.getName(): " + product.getName());
        log.info("product.getDescription(): " + product.getDescription());
        log.info("product.getPrice(): " + product.getPrice());
        log.info("product.getCreatedAt(): " + product.getCreatedAt());
        log.info("product.getCreatedBy(): " + product.getCreatedBy());

        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.toDto(savedProduct);
    }

}
