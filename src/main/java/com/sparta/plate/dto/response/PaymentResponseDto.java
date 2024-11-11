package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Long paymentId;
    private Long orderId;
    private String paymentNumber;
    private Boolean isPaid;
    private Long amount;

    private Timestamped timestamped;
}
