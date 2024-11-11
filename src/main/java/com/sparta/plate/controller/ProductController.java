package com.sparta.plate.controller;

import com.sparta.plate.dto.request.ProductQuantityRequestDto;
import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponseDto createProduct(@RequestBody ProductRequestDto requestDto) {
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
                .message("삭제 완료되었습니다.")
                .build();
    }

    @PatchMapping("/{productId}/inventory")
    public ApiResponseDto updateStockAndLimit(@PathVariable UUID productId, @RequestBody ProductQuantityRequestDto requestDto) {
        productService.updateStockAndLimit(productId, requestDto);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(Map.of("id", productId, "message", "수정 완료되었습니다."))
                .build();
    }
}