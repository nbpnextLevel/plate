package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private String productId;
    private String storeId;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private String displayStatus;
    private int maxOrderLimit;
    private int stockQuantity;

    @Builder.Default
    private List<ProductImageResponseDto> productImageList = new ArrayList<>();

    public static ProductResponseDto toDto(Product product) {
        return ProductResponseDto.builder()
                .productId(String.valueOf(product.getProductId()))
                .storeId(String.valueOf(product.getStoreId()))
                .productName(product.getName())
                .productDescription(product.getDescription())
                .price(product.getPrice())
                .displayStatus(String.valueOf(product.getDisplayStatus()))
                .maxOrderLimit(product.getMaxOrderLimit())
                .stockQuantity(product.getStockQuantity())
                .productImageList(
                        product.getProductImageList().stream()
                                .map(ProductImageResponseDto::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }
}