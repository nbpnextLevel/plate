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
public class PaymentRequestDto {

    private UUID orderId;
    private Long orderPrice;
    private boolean isCanceled;

    public PaymentRequestDto(UUID orderId, Long orderPrice, boolean isCanceled) {
        this.orderId = orderId;
        this.orderPrice = orderPrice;
        this.isCanceled = isCanceled;
    }


}