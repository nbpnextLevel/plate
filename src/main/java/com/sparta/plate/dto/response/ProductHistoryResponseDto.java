package com.sparta.plate.dto.response;

import com.sparta.plate.entity.ProductHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHistoryResponseDto {
    private UUID id;
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    public static ProductHistoryResponseDto toDto(ProductHistory history) {
        return ProductHistoryResponseDto.builder()
                .id(history.getId())
                .productId(history.getProductId())
                .name(history.getName())
                .description(history.getDescription())
                .price(history.getPrice())
                .createdAt(history.getCreatedAt())
                .createdBy(history.getCreatedBy())
                .deletedAt(history.getDeletedAt())
                .deletedBy(history.getDeletedBy())
                .build();
    }
}