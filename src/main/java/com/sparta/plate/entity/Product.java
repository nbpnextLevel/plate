package com.sparta.plate.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.plate.dto.request.ProductRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Column(unique = true, nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

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
    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    @OrderBy("isPrimary desc")
    private List<ProductImage> productImageList = new ArrayList<>();

    public static Product toEntity(ProductRequestDto requestDto, UUID productId) {
        ProductDisplayStatusEnum displayStatus = ProductDisplayStatusEnum.fromString(requestDto.getDisplayStatus());

        Product product = Product.builder()
                .productId(productId)
                .name(requestDto.getProductName())
                .description(requestDto.getProductDescription())
                .price(requestDto.getPrice())
                .displayStatus(displayStatus)
                .maxOrderLimit(requestDto.getMaxOrderLimit())
                .stockQuantity(requestDto.getStockQuantity())
                .isHidden(requestDto.isHidden())
                .storeId(UUID.fromString(requestDto.getStoreId()))
                .build();

        // product.setCreatedBy(createdBy);

        List<ProductImage> productImages = requestDto.getImages().stream()
                .map(ProductImage::toEntity)
                .collect(Collectors.toList());
        product.setProductImageList(productImages);

        return product;
    }

    // public void setCreatedBy(Long createdBy) {
    //     this.createdBy = createdBy;
    // }

    @Override
    public void markAsDeleted(Long deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }
}
