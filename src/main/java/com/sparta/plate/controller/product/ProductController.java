package com.sparta.plate.controller.product;

import com.sparta.plate.dto.request.*;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.product.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
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

    @Hidden
    @Operation(summary = "상품 등록",
            description = "상품 및 상품 이미지 등록. OWNER, MANGER, MASTER 수행 가능.")
    @PostMapping(value = "/reg")
    public ApiResponseDto<Map<String, Object>> createProduct(
            @RequestParam(value = "storeId") String storeId,
            @RequestParam(value = "productName") String productName,
            @RequestParam(value = "productDescription") String productDescription,
            @RequestParam(value = "price") BigDecimal price,
            @RequestParam(value = "stockQuantity") Integer stockQuantity,
            @RequestParam(value = "maxOrderLimit") Integer maxOrderLimit,
            @RequestParam(value = "displayStatus", required = false) String displayStatus,
            @RequestParam(value = "isHidden", required = false) Boolean isHidden,
            @RequestPart(value = "files[]", required = false) MultipartFile[] files,
            @RequestParam(value = "primaryImageIndex", required = false) Integer primaryImageIndex
    ) throws IOException {
        if (primaryImageIndex != null && (primaryImageIndex < 0 || primaryImageIndex >= files.length)) {
            throw new IllegalArgumentException("유효하지 않은 primaryImageIndex 입니다.");
        }

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
    @Operation(summary = "상품 단건 조회",
            description = "상품의 고유 아이디를 활용해 조회."
                    + "노출 상태가 판매 대기/종료이거나 숨겨진 경우, 해당 상품은 등록한 OWNER, MANAGER, MASTER만 조회 가능.")
    public ApiResponseDto<ProductResponseDto> getProduct(@PathVariable UUID productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProductResponseDto responseDto = productService.getProduct(productId, userDetails);

        return ApiResponseDto.success(responseDto);
    }

    @GetMapping("/stores/{storeId}")
    @Operation(summary = "가게별 상품 목록 조회",
            description = "노출 상태가 판매 대기/종료이거나 숨겨진 경우, 해당 상품은 등록한 OWNER, MANAGER, MASTER만 조회 가능."
                    + "상품의 등록 기간 및 숨김 여부 검색은 해당 상품은 등록한 OWNER, MANAGER, MASTER만 조회 가능."
                    + "논리적으로 삭제된 상품은 MANAGER, MASTER만 조회 가능.")
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
    @Operation(summary = "상품 목록 조회",
            description = "노출 상태가 판매 대기/종료이거나 숨겨진 경우, 해당 상품은 등록한 OWNER, MANAGER, MASTER만 조회 가능."
                    + "상품의 등록 기간 및 숨김 여부 검색은 해당 상품은 등록한 OWNER, MANAGER, MASTER만 조회 가능."
                    + "논리적으로 삭제된 상품은 MANAGER, MASTER만 조회 가능.")
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
    @Operation(summary = "상품 정보 수정",
            description = "상품의 주요 정보인 상품명, 설명, 가격 수정. 해당 상품을 등록한 OWNER, MANGER, MASTER 수행 가능.")
    public ApiResponseDto<Map<String, Object>> updateProductDetails(@PathVariable UUID productId, @Valid @RequestBody ProductDetailsRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.updateProductDetails(productId, requestDto, userDetails);

        return ApiResponseDto.success(Map.of("message", "상품 정보가 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/delete")
    @Operation(summary = "상품 삭제",
            description = "상품을 논리적으로 삭제. 해당 상품을 등록한 OWNER, MANGER, MASTER 수행 가능.")
    public ApiResponseDto<Map<String, Object>> deleteProduct(@PathVariable UUID productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.deleteProduct(productId, userDetails);

        return ApiResponseDto.success(Map.of("message", "상품이 성공적으로 삭제되었습니다."));
    }

    @PatchMapping("/{productId}/inventory")
    @Operation(summary = "상품 재고 및 최대 주문 수량 변경",
            description = "해당 상품을 등록한 OWNER, MANGER, MASTER 수행 가능.")
    public ApiResponseDto<Map<String, Object>> updateStockAndLimit(@PathVariable UUID productId, @RequestBody ProductQuantityRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.updateStockAndLimit(productId, requestDto, userDetails);

        return ApiResponseDto.success(Map.of("id", productId, "message", "재고와 주문 제한이 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/visibility")
    @Operation(summary = "상품 숨김 여부 처리",
            description = "해당 상품을 등록한 OWNER, MANGER, MASTER 수행 가능.")
    public ApiResponseDto<Map<String, Object>> updateProductVisibility(@PathVariable UUID productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.updateProductVisibility(productId, userDetails);

        return ApiResponseDto.success(Map.of("id", productId, "message", "상품의 가시성이 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/{productId}/display-status")
    @Operation(summary = "상품 노출 상태 변경",
            description = "노출상태를 PENDING_SALE(판매대기), IN_STOCK(판매중), DISCONTINUED(판매중)"
                    + "해당 상품을 등록한 OWNER, MANGER, MASTER 수행 가능.")
    public ApiResponseDto<Map<String, Object>> updateProductDisplayStatus(@PathVariable UUID productId, @RequestParam String displayStatus, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.updateProductDisplayStatus(productId, displayStatus, userDetails);

        return ApiResponseDto.success(Map.of("id", productId, "message", "상품 표시 상태가 성공적으로 수정되었습니다."));
    }

    @Hidden
    @PatchMapping("/{productId}/images")
    @Operation(summary = "상품 이미지 수정 및 관리",
            description = "등록된 상품의 이미지를 삭제하거나 추가" + "해당 상품을 등록한 OWNER, MANAGER, MASTER 수행 가능")
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

        if (pageSize != 10 && pageSize != 30 && pageSize != 50) {
            pageSize = 10;
        }

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