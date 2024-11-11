package com.sparta.plate.service.product;

import com.sparta.plate.dto.request.ProductDetailsRequestDto;
import com.sparta.plate.dto.request.ProductQuantityRequestDto;
import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductDisplayStatusEnum;
import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.exception.ProductNotFoundException;
import com.sparta.plate.exception.ProductValidationException;
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
    private final ProductHistoryService historyService;

    @Transactional
    public UUID createProduct(ProductRequestDto requestDto) {
        requestDto.validatePrimaryImage();

        Product product = Product.toEntity(requestDto);

        validateProductImages(product);

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
    public void updateProductDetails(UUID productId, ProductDetailsRequestDto requestDto) {
        Product product = findProductById(productId);

        if (!historyService.existsProductHistory(productId)) {
            ProductDetailsRequestDto currentDto = new ProductDetailsRequestDto(product.getName(), product.getDescription(), product.getPrice());

            historyService.createProductHistory(currentDto, productId);
        }

        requestDto.setProductName(requestDto.getProductName() == null ? product.getName() : requestDto.getProductName());
        product.setName(requestDto.getProductName() != null ? requestDto.getProductName() : product.getName());

        requestDto.setProductDescription(requestDto.getProductDescription() == null ? product.getDescription() : requestDto.getProductDescription());
        product.setDescription(requestDto.getProductDescription() != null ? requestDto.getProductDescription() : product.getDescription());

        requestDto.setPrice(requestDto.getPrice() == null ? product.getPrice() : requestDto.getPrice());
        product.setPrice(requestDto.getPrice() != null ? requestDto.getPrice() : product.getPrice());


        productRepository.saveAndFlush(product);

        historyService.createProductHistory(requestDto, productId);
    }

    @Transactional
    public void updateStockAndLimit(UUID productId, ProductQuantityRequestDto requestDto) {
        Product product = findProductById(productId);

        product.setMaxOrderLimit(requestDto.getMaxOrderLimit() != null ? requestDto.getMaxOrderLimit() : product.getMaxOrderLimit());
        product.setStockQuantity(requestDto.getStockQuantity() != null ? requestDto.getStockQuantity() : product.getStockQuantity());

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

    private Product findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }

    private void validateProductImages(Product product) {
        for (ProductImage productImage : product.getProductImageList()) {
            if (productImage.getUploadPath() == null || productImage.getUploadPath().isEmpty()) {
                throw new ProductValidationException("Product image URL cannot be null or empty.");
            }
            productImage.setProduct(product);
        }
    }
    // 상품 수정 시 해당 상품을 등록한 Owner인지 확인하는 메소드 추후 구현
}
