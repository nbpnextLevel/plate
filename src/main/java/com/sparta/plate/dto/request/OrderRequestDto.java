package com.sparta.plate.dto.request;

import com.sparta.plate.entity.OrderStatusEnum;
import com.sparta.plate.entity.OrderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    private UUID orderId;
    private Long userId;
    private UUID storeId;
    private OrderTypeEnum orderType;
    private Long orderPrice;
    private Boolean isCanceled;
    private String orderAddress;
    private String orderRequest;
    private OrderStatusEnum orderStatus;

}
