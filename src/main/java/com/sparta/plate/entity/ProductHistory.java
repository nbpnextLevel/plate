package com.sparta.plate.entity;

import com.sparta.plate.dto.request.ProductRequestDto;
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
public class ProductHistory extends ProductTimestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 10)
    private BigDecimal price;

    public static ProductHistory toEntity(ProductRequestDto requestDto, Long createdBy, UUID productId) {
        ProductHistory product = ProductHistory.builder()
                .productId(productId)
                .name(requestDto.getProductName())
                .description(requestDto.getProductDescription())
                .price(requestDto.getPrice())
                .build();

        product.setCreatedBy(createdBy);


        return product;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}