package com.sparta.plate.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.plate.dto.request.ProductRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false, length = 500)
    private String description;

    @Setter
    @Column(nullable = false, precision = 10)
    private BigDecimal price;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductDisplayStatusEnum displayStatus = ProductDisplayStatusEnum.PENDING_SALE;

    @Setter
    @Column(nullable = false)
    private int maxOrderLimit;

    @Setter
    @Column(nullable = false)
    private int stockQuantity;

    @Setter
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isHidden;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @Setter
    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("isPrimary desc")
    private List<ProductImage> productImages = new ArrayList<>();

    public static Product toEntity(ProductRequestDto requestDto, Store store) {
        ProductDisplayStatusEnum displayStatus = ProductDisplayStatusEnum.fromString(requestDto.getDisplayStatus());

        return Product.builder()
                .name(requestDto.getProductName())
                .description(requestDto.getProductDescription())
                .price(requestDto.getPrice())
                .displayStatus(displayStatus)
                .maxOrderLimit(requestDto.getMaxOrderLimit())
                .stockQuantity(requestDto.getStockQuantity())
                .isHidden(requestDto.isHidden())
                .store(store)
                .build();
    }

    @Override
    public void markAsDeleted(Long deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }
}