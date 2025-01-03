package com.sparta.plate.controller.product;

import com.sparta.plate.dto.request.ProductSuggestionQueryDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.ProductSuggestionResponseDto;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.product.ProductSuggestionService;
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
@RequestMapping("/api/products/suggestion")
public class ProductSuggestionController {

    private final ProductSuggestionService suggestionService;

    @GetMapping("/")
    @Operation(summary = "상품 정보 제안 요청",
            description = "상품 이미지의 고유 아이디를 활용해 논리적으로 삭제." +
                    "OWNER, MANAGER, MASTER 수행 가능")
    public ApiResponseDto<Map<String, Object>> getProductSuggestion(@RequestParam String text) {
        String responseText = suggestionService.getProductSuggestion(text, LocalDateTime.now());

        return ApiResponseDto.success(Map.of("responseText", responseText));
    }

    @GetMapping("/history")
    @Operation(summary = "상품 정보 제안 요청 목록 조회",
            description = "상품 정보 제안 요청 테이블의 이록을 조회. MANAGER, MASTER 수행 가능.")
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

        if (pageSize != 10 && pageSize != 30 && pageSize != 50) {
            pageSize = 10;
        }

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
    @Operation(summary = "상품 정보 제안 요청 기록 단건 삭제",
            description = "상품 정보 제안 요청 기록의 고유 아이디를 활용해 논리적으로 삭제. MASTER 수행 가능.")
    public ApiResponseDto<Map<String, Object>> deleteProductSuggestion(@PathVariable UUID suggestionId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        suggestionService.deleteProductSuggestion(suggestionId, userDetails.getUser().getId());

        return ApiResponseDto.success(Map.of("message", "상품 정보 제안 이력이 성공적으로 삭제되었습니다."));
    }
}