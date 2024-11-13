package com.sparta.plate.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductHistoryQueryDto {
    private UUID id;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sort;
    private int pageNumber;
    private int pageSize;
}