package com.sparta.plate.dto.response;

import com.sparta.plate.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponseDto {
    private String imageId;
    private String fileName;
    private String uploadPath;
    private boolean isPrimary;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    public static ProductImageResponseDto toDto(ProductImage image) {
        return ProductImageResponseDto.builder()
                .imageId(String.valueOf(image.getId()))
                .fileName(image.getFileName())
                .uploadPath(image.getUploadPath())
                .isPrimary(image.isPrimary())
                .isDeleted(image.isDeleted())
                .createdAt(image.getCreatedAt())
                .createdBy(image.getCreatedBy())
                .deletedAt(image.getDeletedAt())
                .deletedBy(image.getDeletedBy())
                .build();
    }
}
