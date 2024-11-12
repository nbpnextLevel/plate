package com.sparta.plate.controller;

import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.service.product.ProductSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/suggestion")
public class ProductSuggestionController {

    private final ProductSuggestionService suggestionService;

    @GetMapping
    public ApiResponseDto getProductSuggestion(@RequestBody String text) {
        String responseText = suggestionService.getProductSuggestion(text, LocalDateTime.now());

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(Map.of("responseText", responseText))
                .build();
    }

    @PatchMapping("/{suggestionId}/delete")
    public ApiResponseDto deleteProductSuggestion(@PathVariable UUID suggestionId, Long userId) {
        suggestionService.deleteProductSuggestion(suggestionId, 1L);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message("삭제 완료되었습니다.")
                .build();
    }
}