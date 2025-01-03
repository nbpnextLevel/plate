package com.sparta.plate.dto.response;

import com.sparta.plate.entity.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@NoArgsConstructor
public class PaymentResponseDto {

    private UUID paymentId;
    private UUID orderId;
    private UUID storeId;
    private String loginId;
    private Long userId;

    private String paymentNumber;
    private Boolean isPaid;
    private Long amount;

    // 가게명, 결제 상품, 주문 유형, 주문 주소, 결제 금액
    private String storeName;
    private OrderTypeEnum orderTypeEnum;
    private String address;
    private Long orderPrice;

    private Long createBy;
    private LocalDateTime createAt;
    private Long deletedBy;
    private LocalDateTime deletedAt;


    public PaymentResponseDto(Order order, Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.orderId = payment.getOrder().getOrderId();
        this.storeId = payment.getOrder().getStore().getId();
        this.loginId = payment.getOrder().getUser().getLoginId();
        this.userId = payment.getOrder().getUser().getId();

        this.paymentNumber = payment.getPaymentNumber();
        this.isPaid = payment.isPaid();
        this.amount = payment.getAmount();

        this.storeName = payment.getOrder().getStore().getStoreName();
        this.orderTypeEnum = payment.getOrder().getOrderType();
        this.address = payment.getOrder().getOrderAddress();
        this.orderPrice = payment.getOrder().getOrderPrice();

        this.createBy = payment.getOrder().getCreateBy();
        this.createAt = payment.getOrder().getCreatedAt();
        this.deletedBy = payment.getOrder().getDeletedBy();
        this.deletedAt = payment.getOrder().getDeletedAt();
    }

    public PaymentResponseDto(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.orderId = payment.getOrder().getOrderId();
        this.storeId = payment.getOrder().getStore().getId();
        this.loginId = payment.getOrder().getUser().getLoginId();
        this.userId = payment.getOrder().getUser().getId();

        this.paymentNumber = payment.getPaymentNumber();
        this.isPaid = payment.isPaid();
        this.amount = payment.getAmount();

        this.storeName = payment.getOrder().getStore().getStoreName();
        this.orderTypeEnum = payment.getOrder().getOrderType();
        this.address = payment.getOrder().getOrderAddress();
        this.orderPrice = payment.getOrder().getOrderPrice();

        this.createBy = payment.getOrder().getCreateBy();
        this.createAt = payment.getOrder().getCreatedAt();
        this.deletedBy = payment.getOrder().getDeletedBy();
        this.deletedAt = payment.getOrder().getDeletedAt();
    }

}