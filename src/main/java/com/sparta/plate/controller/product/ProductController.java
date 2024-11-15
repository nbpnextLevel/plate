package com.sparta.plate.controller.product;

import com.sparta.plate.dto.request.*;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .storeId(UUID.fromString(storeId))
                .productName(productName)
                .productDescription(productDescription)
                .price(price)
                .stockQuantity(stockQuantity)
                .maxOrderLimit(maxOrderLimit)
                .displayStatus(displayStatus)
                .isHidden(isHidden)
                .build();

        if (files != null && files.length != 0) {
            ProductImageRequestDto imageRequestDto = ProductImageRequestDto.builder()
                    .files(files)
                    .primaryImageIndex(primaryImageIndex)
                    .build();
            requestDto.setImages(imageRequestDto);
        }

        UUID savedProductId = productService.createProduct(requestDto);

        return ApiResponseDto.success(Map.of("id", savedProductId));
    }

    @GetMapping("/{productId}")
    public ApiResponseDto<ProductResponseDto> getProduct(@PathVariable UUID productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProductResponseDto responseDto = productService.getProduct(productId, userDetails);

        return ApiResponseDto.success(responseDto);
    }

    @GetMapping("/stores/{storeId}")
    public ApiResponseDto<List<ProductResponseDto>> searchStroesProducts(
            @RequestParam(value = "productId", required = false) UUID productId,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "displayStatus", required = false) String displayStatus,
            @RequestParam(value = "isHidden", required = false) String isHidden,
            @RequestParam(value = "isDeleted", required = false) String isDeleted,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID storeId) {
        return getSearchAllProductsApiResponse(storeId, productId, productName, displayStatus, isHidden, isDeleted, startDate, endDate, sort, pageNumber, pageSize, userDetails);
    }

    @GetMapping("/search")
    public ApiResponseDto<List<ProductResponseDto>> searchProducts(
            @RequestParam(value = "storeId", required = false) UUID storeId,
            @RequestParam(value = "productId", required = false) UUID productId,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "displayStatus", required = false) String displayStatus,
            @RequestParam(value = "isHidden", required = false) String isHidden,
            @RequestParam(value = "isDeleted", required = false) String isDeleted,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return getSearchAllProductsApiResponse(storeId, productId, productName, displayStatus, isHidden, isDeleted, startDate, endDate, sort, pageNumber, pageSize, userDetails);
    }

    @PatchMapping("/{productId}")
    public ApiResponseDto<Map<String, Object>> updateProductDetails(@PathVariable UUID productId, @Valid @RequestBody ProductDetailsRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.updateProductDetails(productId, requestDto, userDetails);

        return ApiResponseDto.success(Map.of("message", "상품 정보가 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/delete")
    public ApiResponseDto<Map<String, Object>> deleteProduct(@PathVariable UUID productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.deleteProduct(productId, userDetails);

        return ApiResponseDto.success(Map.of("message", "상품이 성공적으로 삭제되었습니다."));
    }

    @PatchMapping("/{productId}/inventory")
    public ApiResponseDto<Map<String, Object>> updateStockAndLimit(@PathVariable UUID productId, @RequestBody ProductQuantityRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.updateStockAndLimit(productId, requestDto, userDetails);

        return ApiResponseDto.success(Map.of("id", productId, "message", "재고와 주문 제한이 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/visibility")
    public ApiResponseDto<Map<String, Object>> updateProductVisibility(@PathVariable UUID productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.updateProductVisibility(productId, userDetails);

        return ApiResponseDto.success(Map.of("id", productId, "message", "상품의 가시성이 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/display-status")
    public ApiResponseDto<Map<String, Object>> updateProductDisplayStatus(@PathVariable UUID productId, @RequestParam String displayStatus, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.updateProductDisplayStatus(productId, displayStatus, userDetails);

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
                .primaryImageIndex(primaryImageIndex)
                .deletedImageIds(convertedDeletedImageIds)
                .build();

        if (files != null && files.length != 0) {
            requestDto.setFiles(files);
        }

        productService.manageProductImage(productId, requestDto, userDetails);

        return ApiResponseDto.success(Map.of("id", productId, "message", "상품 이미지가 성공적으로 변경되었습니다."));
    }

    private ApiResponseDto<List<ProductResponseDto>> getSearchAllProductsApiResponse(
            UUID storeId, UUID productId,
            String productName, String displayStatus, String isHidden, String isDeleted,
            LocalDateTime startDate, LocalDateTime endDate, String sort,
            int pageNumber, int pageSize, UserDetailsImpl userDetails) {
        ProductQueryDto requestDto = ProductQueryDto.builder()
                .storeId(storeId)
                .productId(productId)
                .productName(productName)
                .displayStatus(displayStatus)
                .isHidden(isHidden)
                .isDeleted(isDeleted)
                .startDate(startDate)
                .endDate(endDate)
                .sort(sort)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        Page<ProductResponseDto> responseDto = productService.searchProducts(requestDto, userDetails);
        return ApiResponseDto.successPage(responseDto);
    }

}