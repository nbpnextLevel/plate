package com.sparta.plate.entity;

public enum OrderStatusEnum {

    PENDING_PAYMENT,    //결제대기
    DELIVERY_COMPLETED,  //배달완료
    ORDER_CANCELLED;    //주문취소

    public static OrderStatusEnum fromString(String status) {
        for (OrderStatusEnum orderStatus : OrderStatusEnum.values()) {
            if (orderStatus.name().equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatus: " + status);
    }
}
