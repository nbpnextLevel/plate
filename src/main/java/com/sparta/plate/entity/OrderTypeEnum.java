package com.sparta.plate.entity;

public enum OrderTypeEnum {
    DELIVERY,    // 배달
    MEET;      // 대면

    public static OrderTypeEnum fromString(String status) {
        for (OrderTypeEnum orderTypeEnum : OrderTypeEnum.values()) {
            if (orderTypeEnum.name().equalsIgnoreCase(status)) {
                return orderTypeEnum;
            }
        }
        throw new IllegalArgumentException("Invalid OrderTypeStatus: " + status);
    }
}


