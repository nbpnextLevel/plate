package com.sparta.plate.dto.request;

import lombok.Getter;

@Getter
public class ProductImageRequestDto {
    private String fileName;
    private String uploadPath;
    private boolean isPrimary;
}
