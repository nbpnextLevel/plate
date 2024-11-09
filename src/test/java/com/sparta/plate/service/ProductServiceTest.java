package com.sparta.plate.service;

import com.sparta.plate.config.JpaConfig;
import com.sparta.plate.dto.request.ProductImageRequestDto;
import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(JpaConfig.class)
class ProductServiceTest {
    @Autowired
    ProductService productService;

    @Test
    @Transactional
    @Rollback(false)
    @DisplayName("Product 생성 테스트")
    void createProduct() {
        String storeId = UUID.randomUUID().toString();
        String name = "연어 샐러드";
        String description = "설명";
        BigDecimal price = new BigDecimal("18000");
        int maxOrderLimit = 100;
        int stockQuantity = 55;

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .storeId(storeId)
                .productName(name)
                .productDescription(description)
                .price(price)
                .maxOrderLimit(maxOrderLimit)
                .stockQuantity(stockQuantity)
                .build();

        ProductResponseDto product = productService.createProduct(requestDto);

        assertNotNull(product);
        assertEquals(name, product.getProductName());
        assertEquals(description, product.getProductDescription());
        assertEquals(price, product.getPrice());
        assertEquals(maxOrderLimit, product.getMaxOrderLimit());
        assertEquals(stockQuantity, product.getStockQuantity());
    }

    @Test
    @Transactional
    @DisplayName("ProductImage 생성 테스트")
    void createProductImage() {
        String storeId = UUID.randomUUID().toString();
        String name = "연어 샐러드";
        String description = "설명";
        BigDecimal price = new BigDecimal("18000");
        int maxOrderLimit = 100;
        int stockQuantity = 55;

        List<ProductImageRequestDto> images = new ArrayList<>();

        ProductImageRequestDto image1 = ProductImageRequestDto.builder()
                .fileName("salad1")
                .uploadPath("/download/salad1")
                .isPrimary(true)
                .build();
        ProductImageRequestDto image2 = ProductImageRequestDto.builder()
                .fileName("salad1")
                .uploadPath("/download/salad1")
                .isPrimary(false)
                .build();

        images.add(image1);
        images.add(image2);

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .storeId(storeId)
                .productName(name)
                .productDescription(description)
                .price(price)
                .maxOrderLimit(maxOrderLimit)
                .stockQuantity(stockQuantity)
                .images(images)
                .build();

        ProductResponseDto product = productService.createProduct(requestDto);

        assertNotNull(product);
        assertEquals(name, product.getProductName());
        assertEquals(description, product.getProductDescription());
        assertEquals(price, product.getPrice());
        assertEquals(maxOrderLimit, product.getMaxOrderLimit());
        assertEquals(stockQuantity, product.getStockQuantity());
    }
}