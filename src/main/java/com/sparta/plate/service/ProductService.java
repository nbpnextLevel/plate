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

        Product product = Product.toEntity(requestDto, 1L);

        for (ProductImage productImage : product.getProductImageList()) {
            productImage.setProduct(product);
        }

        log.info("product.getName(): " + product.getName());
        log.info("product.getDescription(): " + product.getDescription());
        log.info("product.getPrice(): " + product.getPrice());
        log.info("product.getCreatedAt(): " + product.getCreatedAt());
        log.info("product.getCreatedBy(): " + product.getCreatedBy());
        if (!product.getProductImageList().isEmpty()) {
            log.info("product.getProductImageList().get(0).getFileName(): " + product.getProductImageList().get(0).getFileName());
        }

        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.toDto(savedProduct);
    }


}
