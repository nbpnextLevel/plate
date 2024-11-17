package com.sparta.plate.dto.request;

import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.OrderProduct;
import com.sparta.plate.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductRequestDto {

    private UUID orderProductId;
    private UUID orderId;
    private UUID productId;
    private int orderQuantity;
    private String flagStatus;

    // DTO -> Entity 변환
    public OrderProduct toEntity(Product product, Order order) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(orderProductId);
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        orderProduct.setOrderQuantity(orderQuantity);
        return orderProduct;
    }


}
