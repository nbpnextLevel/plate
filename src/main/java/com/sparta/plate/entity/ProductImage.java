package com.sparta.plate.entity;

import com.sparta.plate.dto.request.ProductImageRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_product_image")
public class ProductImage extends ProductTimestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String uploadPath;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isPrimary;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public static ProductImage toEntity(ProductImageRequestDto requestDto) {
        // image.setCreatedBy(createdBy);
        return ProductImage.builder()
                .fileName(requestDto.getFileName())
                .uploadPath(requestDto.getUploadPath())
                .isPrimary(requestDto.isPrimary())
                .build();
    }

    // public void setCreatedBy(Long createdBy) {
    //     this.createdBy = createdBy;
    // }

    @Override
    public void markAsDeleted(Long deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }

    public void setProduct(Product product) {
        if (this.product != null) {
            throw new IllegalStateException("이미 Product가 설정되었습니다.");
        }
        this.product = product;
    }
}
