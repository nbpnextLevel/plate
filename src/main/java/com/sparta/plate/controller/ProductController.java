package com.sparta.plate.controller;

import com.sparta.plate.dto.request.ProductDetailsRequestDto;
import com.sparta.plate.dto.request.ProductImageRequestDto;
import com.sparta.plate.dto.request.ProductQuantityRequestDto;
import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponseDto createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        UUID savedProductId = productService.createProduct(requestDto);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(Map.of("id", savedProductId))
                .build();
    }

    @PatchMapping("/{productId}/delete")
    public ApiResponseDto deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId, 1L);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message("상품이 성공적으로 삭제되었습니다.")
                .build();
    }

    @PatchMapping("/{productId}")
    public ApiResponseDto updateProductDetails(@PathVariable UUID productId, @Valid @RequestBody ProductDetailsRequestDto requestDto) {
        productService.updateProductDetails(productId, requestDto);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message("상품 정보가 성공적으로 수정되었습니다.")
                .build();
    }

    @PatchMapping("/{productId}/inventory")
    public ApiResponseDto updateStockAndLimit(@PathVariable UUID productId, @RequestBody ProductQuantityRequestDto requestDto) {
        productService.updateStockAndLimit(productId, requestDto);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(Map.of("id", productId, "message", "재고와 주문 제한이 성공적으로 수정되었습니다."))
                .build();
    }

    @PatchMapping("/{productId}/visibility")
    public ApiResponseDto updateProductVisibility(@PathVariable UUID productId) {
        productService.updateProductVisibility(productId);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(Map.of("id", productId, "message", "상품의 가시성이 성공적으로 수정되었습니다."))
                .build();
    }

    @PatchMapping("/{productId}/display-status")
    public ApiResponseDto updateProductDisplayStatus(@PathVariable UUID productId, @RequestParam String displayStatus) {
        productService.updateProductDisplayStatus(productId, displayStatus);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(Map.of("id", productId, "message", "상품 표시 상태가 성공적으로 수정되었습니다."))
                .build();
    }

    @PatchMapping("/{productId}/images")
    public ApiResponseDto manageProductImage(@PathVariable UUID productId, @RequestBody List<ProductImageRequestDto> images) {
        productService.manageProductImage(productId, images, 1L);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(Map.of("id", productId, "message", "상품 표시 상태가 성공적으로 수정되었습니다."))
                .build();
    }
}