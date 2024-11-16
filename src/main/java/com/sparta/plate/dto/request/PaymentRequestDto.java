package com.sparta.plate.dto.request;

import com.sparta.plate.entity.User;
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
    private Long userId;
    private boolean isCanceled; // 주문이 취소 됐냐 안 됐냐

    public PaymentRequestDto(Long userId, UUID orderId, Long orderPrice, boolean isCanceled) {
        this.userId = userId;
        this.orderId = orderId;
        this.orderPrice = orderPrice;
        this.isCanceled = isCanceled;
    }
}
