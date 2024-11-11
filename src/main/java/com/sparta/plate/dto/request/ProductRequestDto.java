package com.sparta.plate.dto.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ProductRequestDto {
    private String storeId;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private String displayStatus;
    private int maxOrderLimit;
    private int stockQuantity;
    private boolean isHidden;

    private List<ProductImageRequestDto> images;

    public void validatePrimaryImage() {
        long primaryCount = images.stream()
                .filter(ProductImageRequestDto::isPrimary)
                .count();
        if (primaryCount != 1) {
            throw new IllegalArgumentException("대표 이미지는 하나여야 합니다.");
        }
    }
}