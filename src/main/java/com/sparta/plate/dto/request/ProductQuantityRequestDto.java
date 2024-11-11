package com.sparta.plate.dto.request;

import lombok.Getter;

@Getter
public class ProductQuantityRequestDto {
    private int maxOrderLimit;
    private int stockQuantity;
}
