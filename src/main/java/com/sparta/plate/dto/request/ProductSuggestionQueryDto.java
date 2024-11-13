package com.sparta.plate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductSuggestionQueryDto {
    private UUID id;
    private String requestText;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sort;
    private int pageNumber;
    private int pageSize;
}