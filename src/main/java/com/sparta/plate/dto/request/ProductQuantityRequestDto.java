package com.sparta.plate.dto.request;

import lombok.Getter;

@Getter
public class ProductQuantityRequestDto {
    private Integer maxOrderLimit;
    private Integer stockQuantity;
}
