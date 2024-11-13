package com.sparta.plate.controller.product;

import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.service.product.ProductHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/histories")
public class ProductHistoryController {

    private final ProductHistoryService historyService;

    @PatchMapping("/{historyId}/delete")
    public ApiResponseDto<Map<String, Object>> deleteProductHistory(@PathVariable UUID historyId, Long userId) {
        historyService.deleteProductHistory(historyId, 1L);

        return ApiResponseDto.success(Map.of("message", "상품 이력이 성공적으로 삭제되었습니다."));
    }
}