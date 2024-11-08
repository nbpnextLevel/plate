package com.sparta.plate.dto.response;

import com.sparta.plate.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponseDto {
    private String imageId;
    private String fileName;
    private String uploadPath;
    private boolean isPrimary;

    public static ProductImageResponseDto toDto(ProductImage productImage) {
        return ProductImageResponseDto.builder()
                .imageId(String.valueOf(productImage.getId()))
                .fileName(productImage.getFileName())
                .uploadPath(productImage.getUploadPath())
                .isPrimary(productImage.isPrimary())
                .build();
    }
}
