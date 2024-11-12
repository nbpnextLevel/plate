package com.sparta.plate.service.product;

import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.repository.ProductHistoryRepository;
import com.sparta.plate.repository.ProductImageRepository;
import com.sparta.plate.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductHistoryRepository historyRepository;
    private final ProductImageRepository imageRepository;

    @Transactional
    public void deleteProductImage(UUID imageId, Long userId) {
        ProductImage image = imageRepository.findById(imageId).orElse(null);

        if (image != null) {
            image.markAsDeleted(userId);
            imageRepository.save(image);
        }
    }

}
