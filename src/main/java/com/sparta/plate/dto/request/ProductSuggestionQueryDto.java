package com.sparta.plate.dto.request;

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
public class ProductSuggestionQueryDto {
    private UUID id;
    private String requestText;
    private String isDeleted;
    private String isSuccess;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sort;
    private Integer pageNumber;
    private Integer pageSize;
}