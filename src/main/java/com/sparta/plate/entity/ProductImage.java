package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_product_image")
public class ProductImage extends TimestampedCreationDeletion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter
    @Column(nullable = false)
    private String fileName;

    @Setter
    @Column(nullable = false)
    private String uploadPath;

    @Setter
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isPrimary;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public static ProductImage toEntity(Product product, String fileName, String uploadPath, boolean isPrimary) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null when creating ProductImage.");
        }

        return ProductImage.builder()
                .product(product)
                .fileName(fileName)
                .uploadPath(uploadPath)
                .isPrimary(isPrimary)
                .build();
    }

    @Override
    public void markAsDeleted(Long deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isPrimary = false;
        this.isDeleted = true;
    }

    public void setProduct(Product product) {
        if (this.product != null) {
            return;
        }
        this.product = product;
    }
}