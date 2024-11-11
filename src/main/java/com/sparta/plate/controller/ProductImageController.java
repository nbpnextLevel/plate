package com.sparta.plate.controller;

import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.service.product.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/images")
public class ProductImageController {

    private final ProductImageService imageService;

    @PatchMapping("/{imageId}/delete")
    public ApiResponseDto deleteProductImage(@PathVariable UUID imageId, Long userId) {
        imageService.deleteProductImage(imageId, 1L);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message("삭제 완료되었습니다.")
                .build();
    }
}