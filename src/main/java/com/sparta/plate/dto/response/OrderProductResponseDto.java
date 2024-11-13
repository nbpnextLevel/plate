package com.sparta.plate.dto.response;

import com.sparta.plate.entity.OrderProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderProductResponseDto {

    private UUID orderProductId;
    //private UUID orderId;
    //private UUID productId;
    private String productName;
    private int orderQuantity;

    public static OrderProductResponseDto fromEntity(OrderProduct orderProduct) {
        OrderProductResponseDto dto = new OrderProductResponseDto();
        dto.setOrderProductId(orderProduct.getOrderProductId());
        //dto.setOrderId(orderProduct.getOrder().getOrderId());
        //dto.setProductId(orderProduct.getProduct().getId());
        dto.setProductName(orderProduct.getProduct().getName());
        dto.setOrderQuantity(orderProduct.getOrderQuantity());
        return dto;
    }

}