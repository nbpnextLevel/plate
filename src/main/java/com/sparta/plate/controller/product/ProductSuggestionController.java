package com.sparta.plate.controller.product;

import com.sparta.plate.dto.request.ProductSuggestionQueryDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.ProductSuggestionResponseDto;
import com.sparta.plate.service.product.ProductSuggestionService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ApiResponseDto<Map<String, Object>> getSuggestionsHistories(@RequestBody ProductSuggestionQueryDto requestDto) {
        List<ProductSuggestionResponseDto> responseDto = suggestionService.getSuggestionsHistories(requestDto);

        return ApiResponseDto.success(Map.of("suggestionsHistories", responseDto));
    }

    @PatchMapping("/{suggestionId}/delete")
    public ApiResponseDto<Map<String, Object>> deleteProductSuggestion(@PathVariable UUID suggestionId, Long userId) {
        suggestionService.deleteProductSuggestion(suggestionId, 1L);

        return ApiResponseDto.success(Map.of("message", "상품 정보 제안 이력이 성공적으로 삭제되었습니다."));
    }
}