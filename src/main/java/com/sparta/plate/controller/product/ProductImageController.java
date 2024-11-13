package com.sparta.plate.controller.product;

import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.service.product.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/images")
public class ProductImageController {

    private final ProductImageService imageService;

    @PatchMapping("/{imageId}/delete")
    public ApiResponseDto<Map<String, Object>> deleteProductImage(@PathVariable UUID imageId, Long userId) {
        imageService.deleteProductImage(imageId, 1L);

        return ApiResponseDto.success(Map.of("message", "상품 이미지가 성공적으로 삭제되었습니다."));
    }
}