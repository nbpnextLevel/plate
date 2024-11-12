package com.sparta.plate.service.product;

import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.exception.ProductImageNotFoundException;
import com.sparta.plate.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository imageRepository;

    @Transactional
    public void deleteProductImage(UUID imageId, Long userId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ProductImageNotFoundException("Product image not found with ID: " + imageId));

        image.markAsDeleted(userId);
        imageRepository.save(image);
    }

    public List<ProductImage> getActiveImages(UUID productId) {
        return imageRepository.findActiveImagesByProductId(productId);
    }

}