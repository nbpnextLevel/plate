package com.sparta.plate.controller.product;

import com.sparta.plate.dto.request.ProductHistoryQueryDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.ProductHistoryResponseDto;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.product.ProductHistoryService;
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
@RequestMapping("/api/products/histories")
public class ProductHistoryController {

    private final ProductHistoryService historyService;

    @PatchMapping("/{historyId}/delete")
    @Operation(summary = "상품 이력 단건 삭제",
            description = "상품 이력의 고유 아이디를 활용해 논리적으로 삭제. MASTER 수행 가능")
    public ApiResponseDto<Map<String, Object>> deleteProductHistory(@PathVariable UUID historyId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        historyService.deleteProductHistory(historyId, userDetails.getUser().getId());

        return ApiResponseDto.success(Map.of("message", "상품 이력이 성공적으로 삭제되었습니다."));
    }

    @GetMapping
    @Operation(summary = "상품 이력 목록 조회",
            description = "상품 이력 테이블의 목록을 조회. MANAGER, MASTER 수행 가능")
    public ApiResponseDto<List<ProductHistoryResponseDto>> getProductHistories(
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam(value = "productId", required = false) UUID productId,
            @RequestParam(value = "isDeleted", required = false) String isDeleted,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        ProductHistoryQueryDto requestDto = ProductHistoryQueryDto.builder()
                .id(id)
                .productId(productId)
                .isDeleted(isDeleted)
                .startDate(startDate)
                .endDate(endDate)
                .sort(sort)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        Page<ProductHistoryResponseDto> responseDto = historyService.getProductHistories(requestDto);
        return ApiResponseDto.successPage(responseDto);
    }
}