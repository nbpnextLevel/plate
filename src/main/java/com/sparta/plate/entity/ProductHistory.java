package com.sparta.plate.entity;

import com.sparta.plate.dto.request.ProductDetailsRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_product_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHistory extends TimestampedCreationDeletion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 10)
    private BigDecimal price;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    public static ProductHistory toEntity(ProductDetailsRequestDto requestDto, UUID productId) {
        return ProductHistory.builder()
                .productId(productId)
                .name(requestDto.getProductName())
                .description(requestDto.getProductDescription())
                .price(requestDto.getPrice())
                .build();
    }

    @Override
    public void markAsDeleted(Long deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }
}
