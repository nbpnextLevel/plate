package com.sparta.plate.entity;

import com.sparta.plate.dto.request.ProductRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_product_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHistory extends Timestamped {

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

    @Transient
    private LocalDateTime updateAt;

    @Transient
    private Long updateBy;

    public static ProductHistory toEntity(ProductRequestDto requestDto, Long createdBy, UUID productId) {
        ProductHistory product = ProductHistory.builder()
                .productId(productId)
                .name(requestDto.getProductName())
                .description(requestDto.getProductDescription())
                .price(requestDto.getPrice())
                .build();

        return product;
    }
    
}
