package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Timestamped;


public class PaymentResponseDto {
    private Long paymentId;
    private Long orderId;
    private String paymentNumber;
    private Boolean isPaid;
    private Long amount;
    private Timestamped createdAt;
    private Long createdBy;
}
