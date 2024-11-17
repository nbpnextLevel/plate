package com.sparta.plate.controller.product;

import com.sparta.plate.dto.request.ProductImageQueryDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.ProductImageResponseDto;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.product.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/images")
public class ProductImageController {

    private final ProductImageService imageService;

    @PatchMapping("/{imageId}/delete")
    @Operation(summary = "상품 이미지 단건 삭제",
            description = "상품 이미지의 고유 아이디를 활용해 논리적으로 삭제. MASTER 수행 가능.")
    public ApiResponseDto<Map<String, Object>> deleteProductImage(@PathVariable UUID imageId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        imageService.deleteProductImage(imageId, userDetails);

        return ApiResponseDto.success(Map.of("message", "상품 이미지가 성공적으로 삭제되었습니다."));
    }

    @GetMapping
    @Operation(summary = "상품 이미지 목록 조회",
            description = "상품 이미지 테이블의 기록을 조회. MANAGER, MASTER 수행 가능.")
    public ApiResponseDto<List<ProductImageResponseDto>> getProductImages(
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam(value = "productId", required = false) UUID productId,
            @RequestParam(value = "isDeleted", required = false) String isDeleted,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        ProductImageQueryDto requestDto = ProductImageQueryDto.builder()
                .id(id)
                .productId(productId)
                .isDeleted(isDeleted)
                .startDate(startDate)
                .endDate(endDate)
                .sort(sort)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        Page<ProductImageResponseDto> responseDto = imageService.getProductImages(requestDto);
        return ApiResponseDto.successPage(responseDto);
    }
}