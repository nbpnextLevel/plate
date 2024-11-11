package com.sparta.plate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ProductImageRequestDto {
    private String fileName;
    private String uploadPath;
    @JsonProperty("isPrimary")
    private boolean isPrimary;
}