package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Payment;
import com.sparta.plate.entity.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private UUID paymentId;
    private UUID orderId;
    private String paymentNumber;
    private Boolean isPaid;
    private Long amount;

    private Timestamped timestamped;

    public PaymentResponseDto(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.orderId = payment.getOrder().getOrderId();
        this.paymentNumber = payment.getPaymentNumber();
        this.isPaid = payment.isPaid();
        this.amount = payment.getAmount();
    }
}

