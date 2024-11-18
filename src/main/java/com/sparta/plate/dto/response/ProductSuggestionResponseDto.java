package com.sparta.plate.dto.response;

import com.sparta.plate.entity.ProductSuggestionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSuggestionResponseDto {
    private UUID id;
    private String requestText;
    private String responseText;
    private LocalDateTime requestAt;
    private LocalDateTime responseAt;
    private boolean isSuccess;
    private String responseStatus;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    public static ProductSuggestionResponseDto toDto(ProductSuggestionRequest suggestionRequest) {
        return ProductSuggestionResponseDto.builder()
                .id(suggestionRequest.getId())
                .requestText(suggestionRequest.getRequestText())
                .responseText(suggestionRequest.getResponseText())
                .requestAt(suggestionRequest.getRequestAt())
                .responseAt(suggestionRequest.getResponseAt())
                .isSuccess(suggestionRequest.isSuccess())
                .responseStatus(suggestionRequest.getResponseStatus())
                .createdAt(suggestionRequest.getCreatedAt())
                .createdBy(suggestionRequest.getCreatedBy())
                .deletedAt(suggestionRequest.getDeletedAt())
                .deletedBy(suggestionRequest.getDeletedBy())
                .build();
    }
}