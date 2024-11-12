package com.sparta.plate.entity;

public enum ProductDisplayStatusEnum {
    PENDING_SALE,
    IN_STOCK,
    DISCONTINUED;

    public static ProductDisplayStatusEnum fromString(String status) {
        if (status == null) {
            throw new IllegalArgumentException("유효하지 않은 요청값입니다.");
        }

        try {
            return ProductDisplayStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 요청값입니다.");
        }
    }
}