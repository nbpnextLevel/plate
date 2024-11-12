package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private UUID id;
    private String storeId;
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

    public static ProductResponseDto toDto(Product product, List<ProductImage> images) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .storeId(String.valueOf(product.getStoreId()))
                .productName(product.getName())
                .productDescription(product.getDescription())
                .price(product.getPrice())
                .displayStatus(String.valueOf(product.getDisplayStatus()))
                .maxOrderLimit(product.getMaxOrderLimit())
                .stockQuantity(product.getStockQuantity())
                .isHidden(product.isHidden())
                .isDeleted(product.isDeleted())
                .productImageList(
                        images.stream()
                                .map(ProductImageResponseDto::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }
}