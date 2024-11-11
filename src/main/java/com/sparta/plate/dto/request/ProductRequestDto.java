package com.sparta.plate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ProductRequestDto {

    @NotNull(message = "Store ID cannot be null")
    private String storeId;

    @NotBlank(message = "Product name cannot be blank")
    private String productName;

    @NotBlank(message = "Product description cannot be blank")
    private String productDescription;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be a positive value")
    private BigDecimal price;

    private String displayStatus;

    @NotNull(message = "Max order limit cannot be null")
    @Positive(message = "Max order limit must be positive")
    private Integer maxOrderLimit;

    @NotNull(message = "Stock quantity cannot be null")
    @Positive(message = "Stock quantity must be positive")
    private Integer stockQuantity;

    @JsonProperty("isHidden")
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
