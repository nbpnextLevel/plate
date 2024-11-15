package com.sparta.plate.entity;

import com.sparta.plate.dto.request.PaymentRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.mapping.ToOne;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@Table(name = "p_payment")
@NoArgsConstructor
public class Payment extends TimestampedCreationDeletion{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId = UUID.randomUUID();

    @OneToOne(fetch = FetchType.LAZY)
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

    @Builder
    public Payment(UUID paymentId, Order order, String paymentNumber, Long amount) {
        this.paymentId = paymentId;
        this.order = order;
        this.paymentNumber = paymentNumber;
        this.isPaid = true;
        this.amount = amount;
    }

    public Payment(Order order, PaymentRequestDto paymentRequestDto) {
        super();
        this.amount = paymentRequestDto.getOrderPrice() != null ? paymentRequestDto.getOrderPrice() : order.getOrderPrice();
        this.paymentNumber = "PAY_" + UUID.randomUUID().toString();
        this.isPaid = paymentRequestDto.isCanceled(); // DTO에 결제 여부가 있다면 사용
        this.order = order;
    }

    @PrePersist  // 엔티티가 저장되기 전에 UUID 자동 생성
    public void prePersist() {
        if (paymentId == null) {
            paymentId = UUID.randomUUID();
        }
    }
}
