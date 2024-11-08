package com.sparta.plate.entity;

public enum ProductDisplayStatus {
    PENDING_SALE,
    IN_STOCK,
    SOLD_OUT_TODAY,
    OUT_OF_STOCK,
    DISCONTINUED;

    public static ProductDisplayStatus fromString(String status) {
        if (status == null) {
            return PENDING_SALE;
        }

        try {
            return ProductDisplayStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return PENDING_SALE;
        }
    }
}