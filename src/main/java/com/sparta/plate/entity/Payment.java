package com.sparta.plate.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;

    @Column(nullable = false)
    private String paymentNumber;

    @Column(nullable = false)
    private boolean isPaid;

    @Column(nullable = false)
    private Long amount;
}