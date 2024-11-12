package com.sparta.plate.dto.response;

import com.sparta.plate.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
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

    // 가게명, 결제 상품, 주문 유형, 주문 주소, 결제 금액
    private String storeName;
    private List<OrderProduct> orderProductList;
    private OrderTypeEnum orderTypeEum;
    private String address;
    private Long orderPrice;

    private Timestamped timestamped;


    public PaymentResponseDto(Order order, Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.orderId = payment.getOrder().getOrderId();
        this.paymentNumber = payment.getPaymentNumber();
        this.isPaid = payment.isPaid();
        this.amount = payment.getAmount();

        this.storeName = payment.getOrder().getStore().getStoreName();
        this.orderProductList = payment.getOrder().getOrderProductList();
        this.orderTypeEum = payment.getOrder().getOrderType();
        this.address = payment.getOrder().getOrderAddress();
        this.orderPrice = payment.getOrder().getOrderPrice();

        this.timestamped = getTimestamped();
    }
}

