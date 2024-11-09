package com.sparta.plate.entity;

import com.sparta.plate.dto.request.ProductImageRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_product_image")
public class ProductImage extends ProductTimestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public static ProductImage toEntity(ProductImageRequestDto requestDto, Long createdBy) {
        ProductImage image = ProductImage.builder()
                .fileName(requestDto.getFileName())
                .uploadPath(requestDto.getUploadPath())
                .isPrimary(requestDto.isPrimary())
                .build();

        image.setCreatedBy(createdBy);
        return image;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public void markAsDeleted(Long deletedBy) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public void setProduct(Product product) {
        if (this.product != null) {
            throw new IllegalStateException("이미 Product가 설정되었습니다.");
        }
        this.product = product;
    }
}
