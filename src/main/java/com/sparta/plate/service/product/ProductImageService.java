package com.sparta.plate.service.product;

import com.sparta.plate.dto.request.ProductImageRequestDto;
import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.exception.ProductImageNotFoundException;
import com.sparta.plate.repository.ProductImageRepository;
import com.sparta.plate.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository imageRepository;
    private final S3Uploader s3Uploader;

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

    @Transactional
    public List<ProductImage> processProductImages(Product product, ProductImageRequestDto requestDto) throws IOException {
        List<ProductImage> newImages = new ArrayList<>();

        MultipartFile[] files = requestDto.getFiles();
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            boolean isPrimary = isPrimaryImage(requestDto, i);
            String uploadImageUrl = uploadImage(file);
            String uniqueFileName = generateUniqueFileName(file);

            ProductImage productImage = ProductImage.toEntity(product, uniqueFileName, uploadImageUrl, isPrimary);
            newImages.add(productImage);
        }

        return newImages;
    }

    @Transactional
    public List<ProductImage> updatePrimaryImage(List<ProductImage> images, ProductImageRequestDto requestDto) {
        if (requestDto.getPrimaryImageIndex() != null && requestDto.getPrimaryImageIndex() >= 0) {
            images.forEach(image -> image.setPrimary(false));

            if (requestDto.getPrimaryImageIndex() < images.size()) {
                ProductImage selectedImage = images.get(requestDto.getPrimaryImageIndex());
                selectedImage.setPrimary(true);
            }
        }
        return images;
    }

    private boolean isPrimaryImage(ProductImageRequestDto requestDto, int index) {
        return requestDto.getPrimaryImageIndex() != null
                && requestDto.getPrimaryImageIndex() >= 0
                && requestDto.getPrimaryImageIndex() == index;
    }

    private String generateUniqueFileName(MultipartFile file) {
        return UUID.randomUUID() + "_" + file.getOriginalFilename();
    }

    private String uploadImage(MultipartFile file) throws IOException {
        return s3Uploader.upload(file);
    }
}