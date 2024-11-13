package com.sparta.plate.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductSuggestionQueryDto {
    private UUID id;
    private String requestText;
    private String isDeleted;
    private String isSuccess;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sort;
    private int pageNumber;
    private int pageSize;
}