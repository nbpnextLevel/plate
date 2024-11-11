package com.sparta.plate.service;

import com.sparta.plate.config.JpaConfig;
import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductDisplayStatus;
import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.repository.ProductRepository;
import com.sparta.plate.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    @DisplayName("Product 생성 및 저장 테스트")
    void createProductAndSave() {
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .productId(productId)
                .storeId(UUID.randomUUID())
                .name("연어 샐러드")
                .description("설명")
                .price(new BigDecimal("18000"))
                .displayStatus(ProductDisplayStatus.PENDING_SALE)
                .maxOrderLimit(100)
                .stockQuantity(55)
                .isHidden(false)
                .build();

        List<ProductImage> productImages = new ArrayList<>();

        ProductImage image1 = ProductImage.builder()
                .fileName("salad1")
                .uploadPath("/download/salad1")
                .isPrimary(true)
                .build();
        ProductImage image2 = ProductImage.builder()
                .fileName("salad2")
                .uploadPath("/download/salad2")
                .isPrimary(false)
                .build();

        image1.setProduct(product);
        image2.setProduct(product);
        productImages.add(image1);
        productImages.add(image2);

        product.setProductImageList(productImages);

        productRepository.save(product);

        Product savedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertNotNull(savedProduct);
        assertEquals("연어 샐러드", savedProduct.getName());
        assertEquals("설명", savedProduct.getDescription());
        assertEquals(new BigDecimal("18000"), savedProduct.getPrice());
        assertEquals(100, savedProduct.getMaxOrderLimit());
        assertEquals(55, savedProduct.getStockQuantity());
    }
}
