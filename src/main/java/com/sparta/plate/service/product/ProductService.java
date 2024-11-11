package com.sparta.plate.service.product;

import com.sparta.plate.dto.request.ProductQuantityRequestDto;
import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductDisplayStatusEnum;
import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.exception.ProductNotFoundException;
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
    public UUID createProduct(ProductRequestDto requestDto) {
        if (requestDto.getProductName() == null || requestDto.getProductName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }

        UUID productId = generateUniqueProductId();

        Product product = Product.toEntity(requestDto, productId);

        for (ProductImage productImage : product.getProductImageList()) {
            if (productImage.getUploadPath() == null || productImage.getUploadPath().isEmpty()) {
                throw new IllegalArgumentException("Product image URL cannot be null or empty.");
            }
            productImage.setProduct(product);
        }

        Product savedProduct = productRepository.save(product);

        return savedProduct.getId();
    }

    @Transactional
    public void deleteProduct(UUID productId, Long userId) {
        Product product = findProductById(productId);

        product.markAsDeleted(userId);

        productRepository.save(product);
    }

    @Transactional
    public void updateStockAndLimit(UUID productId, ProductQuantityRequestDto requestDto) {
        Product product = findProductById(productId);

        if (requestDto.getMaxOrderLimit() != null) product.setMaxOrderLimit(requestDto.getMaxOrderLimit());
        if (requestDto.getStockQuantity() != null) product.setStockQuantity(requestDto.getStockQuantity());

        productRepository.save(product);
    }

    @Transactional
    public void updateProductVisibility(UUID productId) {
        Product product = findProductById(productId);

        product.setHidden(!product.isHidden());

        productRepository.save(product);
    }

    @Transactional
    public void updateProductDisplayStatus(UUID productId, String displayStatus) {
        Product product = findProductById(productId);

        ProductDisplayStatusEnum status = ProductDisplayStatusEnum.fromString(displayStatus);
        product.setDisplayStatus(status);

        productRepository.save(product);
    }

    private UUID generateUniqueProductId() {
        UUID productId = UUID.randomUUID();
        while (productRepository.existsByProductId(productId)) {
            productId = UUID.randomUUID();
        }
        return productId;
    }

    private Product findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }

    // 상품 수정 시 해당 상품을 등록한 Owner인지 확인하는 메소드 추후 구현
}
