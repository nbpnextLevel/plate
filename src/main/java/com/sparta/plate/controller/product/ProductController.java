package com.sparta.plate.controller.product;

import com.sparta.plate.dto.request.ProductDetailsRequestDto;
import com.sparta.plate.dto.request.ProductImageRequestDto;
import com.sparta.plate.dto.request.ProductQuantityRequestDto;
import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponseDto<Map<String, Object>> createProduct(
            @RequestParam(value = "storeId") String storeId,
            @RequestParam(value = "productName") String productName,
            @RequestParam(value = "productDescription") String productDescription,
            @RequestParam(value = "price") BigDecimal price,
            @RequestParam(value = "stockQuantity") Integer stockQuantity,
            @RequestParam(value = "maxOrderLimit") Integer maxOrderLimit,
            @RequestParam(value = "displayStatus", required = false) String displayStatus,
            @RequestParam(value = "isHidden", required = false) Boolean isHidden,
            @RequestParam(value = "files[]", required = false) MultipartFile[] files,
            @RequestParam(value = "primaryImageIndex", required = false) Integer primaryImageIndex
    ) throws IOException {
        ProductImageRequestDto imageRequestDto = ProductImageRequestDto.builder()
                .files(files)
                .primaryImageIndex(primaryImageIndex)
                .build();

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .storeId(UUID.fromString(storeId))
                .productName(productName)
                .productDescription(productDescription)
                .price(price)
                .stockQuantity(stockQuantity)
                .maxOrderLimit(maxOrderLimit)
                .displayStatus(displayStatus)
                .isHidden(isHidden)
                .images(imageRequestDto)
                .build();

        UUID savedProductId = productService.createProduct(requestDto);

        return ApiResponseDto.success(Map.of("id", savedProductId));
    }

    @PatchMapping("/{productId}/delete")
    public ApiResponseDto<Map<String, Object>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId, 1L);

        return ApiResponseDto.success(Map.of("message", "상품이 성공적으로 삭제되었습니다."));
    }

    @PatchMapping("/{productId}")
    public ApiResponseDto<Map<String, Object>> updateProductDetails(@PathVariable UUID productId, @Valid @RequestBody ProductDetailsRequestDto requestDto) {
        productService.updateProductDetails(productId, requestDto);

        return ApiResponseDto.success(Map.of("message", "상품 정보가 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/inventory")
    public ApiResponseDto<Map<String, Object>> updateStockAndLimit(@PathVariable UUID productId, @RequestBody ProductQuantityRequestDto requestDto) {
        productService.updateStockAndLimit(productId, requestDto);

        return ApiResponseDto.success(Map.of("id", productId, "message", "재고와 주문 제한이 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/visibility")
    public ApiResponseDto<Map<String, Object>> updateProductVisibility(@PathVariable UUID productId) {
        productService.updateProductVisibility(productId);

        return ApiResponseDto.success(Map.of("id", productId, "message", "상품의 가시성이 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/display-status")
    public ApiResponseDto<Map<String, Object>> updateProductDisplayStatus(@PathVariable UUID productId, @RequestParam String displayStatus) {
        productService.updateProductDisplayStatus(productId, displayStatus);

        return ApiResponseDto.success(Map.of("id", productId, "message", "상품 표시 상태가 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/images")
    public ApiResponseDto<Map<String, Object>> manageProductImage(
            @PathVariable UUID productId,
            @RequestParam(value = "files[]", required = false) MultipartFile[] files,
            @RequestParam(value = "primaryImageIndex", required = false) Integer primaryImageIndex,
            @RequestParam(value = "deletedImageIds", required = false) List<String> deletedImageIds,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        List<UUID> convertedDeletedImageIds = Optional.ofNullable(deletedImageIds)
                .map(ids -> ids.stream().map(UUID::fromString).toList())
                .orElseGet(Collections::emptyList);

        ProductImageRequestDto requestDto = ProductImageRequestDto.builder()
                .files(files)
                .primaryImageIndex(primaryImageIndex)
                .deletedImageIds(convertedDeletedImageIds)
                .build();

        productService.manageProductImage(productId, requestDto, userDetails);

        return ApiResponseDto.success(Map.of("id", productId, "message", "상품 이미지가 성공적으로 변경되었습니다."));
    }
}