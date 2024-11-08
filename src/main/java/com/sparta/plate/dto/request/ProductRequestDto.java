package com.sparta.plate.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {
    private String storeId;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private String displayStatus;
    private int maxOrderLimit;
    private int stockQuantity;
    private boolean isHidden;
}