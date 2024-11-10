package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "p_order_product")  // 테이블 이름 설정
@Getter
@Setter
@NoArgsConstructor
public class OrderProduct extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_product_id", updatable = false, nullable = false)
    private UUID orderProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)  // 외래 키 설정 (user_id)
    private Order order; // 주문 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)  // 외래 키 설정 (user_id)
    private Product product; // 상품 ID

    @Column(nullable = false)
    private int orderQuantity; // 주문 수량


    // 기본 생성자와 매개변수화된 생성자 (선택사항)
    public OrderProduct(UUID orderProductId, Order order, Product product, int orderQuantity) {
        this.orderProductId = orderProductId;
        this.order = order;
        this.product = product;
        this.orderQuantity = orderQuantity;
    }

}