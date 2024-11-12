package com.sparta.plate.dto.request;

import com.sparta.plate.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    private UUID orderId;
    private Long orderPrice;

    private boolean isCanceled;

    public PaymentRequestDto(Order order) {
        this.orderId = order.getOrderId();
        this.orderPrice = order.getOrderPrice();
        this.isCanceled = order.getIsCanceled();
    }


}