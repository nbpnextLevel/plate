package com.sparta.plate.service;

import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    ProductService productService;

    @Test
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

        ProductResponseDto product = productService.createProduct(requestDto, 1L);

        assertNotNull(product);
        assertEquals(name, product.getProductName());
        assertEquals(description, product.getProductDescription());
        assertEquals(price, product.getPrice());
        assertEquals(maxOrderLimit, product.getMaxOrderLimit());
        assertEquals(stockQuantity, product.getStockQuantity());
    }
}