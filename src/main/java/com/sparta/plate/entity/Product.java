package com.sparta.plate.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.plate.dto.request.ProductRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "p_product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends ProductTimestamped {

    @Id
    @GeneratedValue
    private UUID productId;

    private UUID storeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 10)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductDisplayStatus displayStatus;

    @Column(nullable = false)
    private int maxOrderLimit;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isHidden;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    @OrderBy("isPrimary desc")
    @JsonManagedReference
    private List<ProductImage> productImageList = new ArrayList<>();

    @PrePersist
    public void generateId() {
        if (productId == null) {
            productId = UUID.randomUUID();
        }
    }

    public Product toEntity(ProductRequestDto requestDto, Long createdBy) {
        Product product = Product.builder()
                .name(requestDto.getProductName())
                .description(requestDto.getProductDescription())
                .price(requestDto.getPrice())
                .displayStatus(ProductDisplayStatus.valueOf(requestDto.getDisplayStatus()))
                .maxOrderLimit(requestDto.getMaxOrderLimit())
                .stockQuantity(requestDto.getStockQuantity())
                .isHidden(requestDto.isHidden())
                .storeId(UUID.fromString(requestDto.getStoreId()))
                .build();

        product.setCreatedBy(createdBy);

        return product;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public void markAsDeleted(Long deletedBy) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
