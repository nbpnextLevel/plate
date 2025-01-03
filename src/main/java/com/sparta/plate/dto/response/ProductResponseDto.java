package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private UUID productId;
    private UUID storeId;
    private String storeName;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private String displayStatus;
    private int maxOrderLimit;
    private int stockQuantity;
    private boolean isHidden;
    private boolean isDeleted;

    @Builder.Default
    private List<ProductImageResponseDto> productImageList = new ArrayList<>();

    public static ProductResponseDto toDto(Product product, List<ProductImageResponseDto> images) {
        return ProductResponseDto.builder()
                .productId(product.getId())
                .storeId(product.getStore().getId())
                .storeName(product.getStore().getStoreName())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .price(product.getPrice())
                .displayStatus(String.valueOf(product.getDisplayStatus()))
                .maxOrderLimit(product.getMaxOrderLimit())
                .stockQuantity(product.getStockQuantity())
                .isHidden(product.isHidden())
                .isDeleted(product.isDeleted())
                .productImageList(images)
                .build();
    }
}