package com.sparta.plate.entity;

import com.sparta.plate.dto.request.PaymentRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Getter
@Setter
@Table(name = "p_payment")
@NoArgsConstructor
public class Payment extends Timestamped{

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId = UUID.randomUUID();

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String paymentNumber;

    @Column(nullable = false)
    private boolean isPaid;

    @Column(nullable = false)
    private Long amount;    // 결제 금액 (주문 금액과 다를 수 있음)



//    public Payment(Order order) {
//        this.paymentId = getPaymentId();
//        this.amount = order.getOrderPrice();
//        this.paymentNumber = "PAY_" + this.paymentId.toString();
//        this.isPaid = true;
//        this.order = order;
//    }
    public Payment(Order order) {
        System.out.println("==========================");
        System.out.println(this.paymentId);
        this.amount = order.getOrderPrice();
        this.paymentNumber = "PAY_" + this.paymentId.toString();
        this.isPaid = true;
        this.order = order;
    }

}