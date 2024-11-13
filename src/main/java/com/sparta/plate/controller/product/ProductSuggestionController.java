package com.sparta.plate.controller.product;

import com.sparta.plate.dto.request.ProductSuggestionQueryDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.ProductSuggestionResponseDto;
import com.sparta.plate.service.product.ProductSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/suggestion")
public class ProductSuggestionController {

    private final ProductSuggestionService suggestionService;

    @GetMapping
    public ApiResponseDto<Map<String, Object>> getProductSuggestion(@RequestParam String text) {
        String responseText = suggestionService.getProductSuggestion(text, LocalDateTime.now());

        return ApiResponseDto.success(Map.of("responseText", responseText));
    }

    @GetMapping("/history")
    public ApiResponseDto<List<ProductSuggestionResponseDto>> getSuggestionsHistories(
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam(value = "requestText", required = false) String requestText,
            @RequestParam(value = "isDeleted", required = false) String isDeleted,
            @RequestParam(value = "isSuccess", required = false) String isSuccess,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        ProductSuggestionQueryDto requestDto = ProductSuggestionQueryDto.builder()
                .id(id)
                .requestText(requestText)
                .isDeleted(isDeleted)
                .isSuccess(isSuccess)
                .startDate(startDate)
                .endDate(endDate)
                .sort(sort)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        Page<ProductSuggestionResponseDto> responseDto = suggestionService.getSuggestionsHistories(requestDto);
        return ApiResponseDto.successPage(responseDto);
    }

    @PatchMapping("/{suggestionId}/delete")
    public ApiResponseDto<Map<String, Object>> deleteProductSuggestion(@PathVariable UUID suggestionId, Long userId) {
        suggestionService.deleteProductSuggestion(suggestionId, 1L);

        return ApiResponseDto.success(Map.of("message", "상품 정보 제안 이력이 성공적으로 삭제되었습니다."));
    }
}