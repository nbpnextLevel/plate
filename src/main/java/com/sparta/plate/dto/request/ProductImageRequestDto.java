package com.sparta.plate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductImageRequestDto {
    private UUID id;
    private String fileName;
    private String uploadPath;
    @JsonProperty("isPrimary")
    private boolean isPrimary;
}