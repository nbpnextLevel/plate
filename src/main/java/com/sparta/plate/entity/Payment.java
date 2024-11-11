package com.sparta.plate.entity;

import com.sparta.plate.dto.request.PaymentRequestDto;
import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String paymentNumber;

    @Column(nullable = false)
    private boolean isPaid;

    @Column(nullable = false)
    private Long amount;



    public Payment(PaymentRequestDto paymentRequestDto, Order order) {
        this.amount = order.getOrderPrice();
        this.order = order;
        this.isPaid = true;
    }

    @PostPersist
    private void generatePaymentNumber(){
        this.paymentNumber = "PAY_" + this.paymentId;
    }
}