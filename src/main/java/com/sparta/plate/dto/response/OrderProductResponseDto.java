package com.sparta.plate.dto.response;

import com.sparta.plate.entity.OrderProduct;
import com.sparta.plate.entity.ProductHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductResponseDto {

    private UUID orderProductId;
    private UUID orderId;
    private UUID productId;
    private UUID productHistoryId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private int orderQuantity;

    public static OrderProductResponseDto fromEntity(OrderProduct orderProduct) {
        OrderProductResponseDto dto = new OrderProductResponseDto();
        dto.setOrderProductId(orderProduct.getOrderProductId());
        dto.setOrderId(orderProduct.getOrder().getOrderId());
        dto.setProductId(orderProduct.getProduct().getId());
        dto.setOrderProductId(orderProduct.getOrderProductId());
        dto.setOrderQuantity(orderProduct.getOrderQuantity());
        return dto;
    }



    public OrderProductResponseDto(OrderProduct orderProduct, ProductHistory productHistory) {
        this.orderProductId = orderProduct.getOrderProductId();
        this.orderId = orderProduct.getOrder().getOrderId();
        this.productId = orderProduct.getProduct().getId();
        this.productHistoryId = orderProduct.getProductHistoryId();
        this.productName = productHistory.getName();
        this.productDescription = productHistory.getDescription();
        this.productPrice = productHistory.getPrice();
        this.orderQuantity = orderProduct.getOrderQuantity();
    }

}