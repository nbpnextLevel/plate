package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sparta.plate.entity.OrderStatusEnum.ORDER_CANCELLED;
import static com.sparta.plate.entity.OrderStatusEnum.PENDING_PAYMENT;

@Entity
@Table(name = "p_order")  // 테이블 이름 설정
@Getter
@Setter
@NoArgsConstructor
public class Order extends Timestamped{

    @Id
    @Column(name = "order_id", columnDefinition = "UUID")
    private UUID orderId;  // 주문 ID (UUID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)  // 외래 키 설정 (user_id)
    private User user;  // 주문한 사용자 (User 엔티티와 연관)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)  // 외래 키 설정 (store_id)
    private Store store;  // 주문이 발생한 상점 (Store 엔티티와 연관)

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderTypeEnum orderType;  // 주문 유형 (ENUM)

    @Column(name = "order_price", nullable = false)
    private Long orderPrice;  // 주문 금액 (NUMERIC(10,0))

    @Column(name = "is_canceled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isCanceled = false;  // 주문 취소 여부 (BOOLEAN)

    @Column(name = "order_address", nullable = false, length = 225)
    private String orderAddress;  // 배송지 주소 (VARCHAR(225))

    @Column(name = "order_request", length = 500)
    private String orderRequest;  // 주문 요청 사항 (VARCHAR(500))

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatusEnum orderStatus = PENDING_PAYMENT;  // 주문 상태 (ENUM)

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted = false; // 삭제 여부 (BOOLEAN)

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)  // Order 엔티티에서 'product' 필드를 관리
    private List<OrderProduct> orderProductList = new ArrayList<>();

    // 주문 가격 계산
    public void calculateTotalPrice() {
        this.orderPrice = orderProductList.stream()
                .mapToLong(OrderProduct::getTotalPrice)
                .sum();
    }

    // 주문 취소
    public void cancelOrder() {
        this.isCanceled = true;
        this.orderStatus = ORDER_CANCELLED;
    }

}