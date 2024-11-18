package com.sparta.plate.dto.request;

import com.sparta.plate.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderRequestDto {

    private UUID orderId;
    private Long userId;
    private UUID storeId;
    private String orderType;
    private Long orderPrice;
    private Boolean isCanceled;
    private String orderAddress;
    private String orderRequest;
    private String orderStatus;
    private List<OrderProductRequestDto> orderProductList;

    // DTO -> Entity 변환
    public Order toEntity(User user, Store store) {
        Order order = new Order();
        order.setUser(user);
        order.setStore(store);
        order.setOrderType(OrderTypeEnum.valueOf(orderType));
        order.setOrderPrice(orderPrice);
        order.setOrderAddress(orderAddress);
        order.setOrderRequest(orderRequest);
        order.setOrderStatus(OrderStatusEnum.valueOf(orderStatus));
        return order;
    }







}
