package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.OrderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private UUID orderId;
    private Long userId;
    private UUID storeId;
    private String storeName;
    private OrderTypeEnum orderType;
    private Long orderPrice;
    private String orderAddress;
    private String orderRequest;
    private String orderStatus;
    private List<OrderProductResponseDto> orderProductList;


    public static OrderResponseDto fromEntity(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getId());
        dto.setStoreId(order.getStore().getId());
        dto.setStoreName(order.getStore().getStoreName());
        dto.setOrderType(order.getOrderType());
        dto.setOrderPrice(order.getOrderPrice());
        dto.setOrderAddress(order.getOrderAddress());
        dto.setOrderRequest(order.getOrderRequest());
        dto.setOrderStatus(order.getOrderStatus().name());
        dto.setOrderProductList(order.getOrderProductList().stream()
                .map(OrderProductResponseDto::fromEntity)
                .toList());
        return dto;
    }

}
