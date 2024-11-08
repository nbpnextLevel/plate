package com.sparta.plate.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "p_payment")
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID payment_id;  // 결제 아이디 (PK)

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order_id;  // 주문 아이디 (FK)

    @Column(nullable = false)
    private String paymentNumber;  // 결제 승인 번호

    @Column(nullable = false)
    private boolean isPaid;  // 결제 여부

    @Column(nullable = false)
    private Long amount;  // 결제 금액

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 생성일

    @Column(nullable = false)
    private String createdBy;  // 생성자
}