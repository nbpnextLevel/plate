package com.sparta.plate.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private Long orderId;
    private Long amount;
}

