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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    @Column(nullable = false)
    private String paymentNumber;

    @Column(nullable = false)
    private boolean isPaid;

    @Column(nullable = false)
    private Long amount;    // 결제 금액 (주문 금액과 다를 수 있음)


    public Payment(Order order) {
        this.amount = order.getOrderPrice();
        this.paymentNumber = "PAY_" + this.paymentId.toString();
        this.isPaid = true;
        this.order = order;
    }

    public Payment(Order order, PaymentRequestDto paymentRequestDto) {
        super();
    }

    @PrePersist  // 엔티티가 저장되기 전에 UUID 자동 생성
    public void prePersist() {
        if (paymentId == null) {
            paymentId = UUID.randomUUID();
        }
    }
}

