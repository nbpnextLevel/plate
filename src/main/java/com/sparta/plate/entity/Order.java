package com.sparta.plate.entity;

import com.sparta.plate.entity.OrderStatusEnum;
import com.sparta.plate.entity.OrderTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_order")  // 테이블 이름 설정
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", columnDefinition = "UUID")
    private UUID orderId;  // 주문 ID (UUID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // 외래 키 설정 (user_id)
    private User user;  // 주문한 사용자 (User 엔티티와 연관)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)  // 외래 키 설정 (store_id)
    private Store store;  // 주문이 발생한 상점 (Store 엔티티와 연관)

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderTypeEnum orderTypeEnum;  // 주문 유형 (ENUM)

    @Column(name = "order_price", nullable = false, precision = 10, scale = 0)
    private Long orderPrice;  // 주문 금액 (NUMERIC(10,0))

    @Column(name = "is_canceled", nullable = false)
    private Boolean isCanceled;  // 주문 취소 여부 (BOOLEAN)

    @Column(name = "order_address", nullable = false, length = 225)
    private String orderAddress;  // 배송지 주소 (VARCHAR(225))

    @Column(name = "order_request", length = 500)
    private String orderRequest;  // 주문 요청 사항 (VARCHAR(500))

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatusEnum orderStatusEnum;  // 주문 상태 (ENUM)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 생성 일시 (TIMESTAMP)

    @Column(name = "created_by", nullable = false)
    private Long createdBy;  // 생성자 (사용자 ID)

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 업데이트 일시 (TIMESTAMP)

    @Column(name = "updated_by")
    private Long updatedBy;  // 업데이트한 사람 (사용자 ID)

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;  // 삭제 일시 (TIMESTAMP)

    @Column(name = "deleted_by")
    private Long deletedBy;  // 삭제한 사람 (사용자 ID)

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;  // 삭제 여부 (BOOLEAN)

    // 기본 생성자와 매개변수화된 생성자 (선택사항)
    public Order(User user, Store store, OrderTypeEnum orderTypeEnum, Long orderPrice, Boolean isCanceled,
                 String orderAddress, String orderRequest, OrderStatusEnum orderStatusEnum, Long createdBy) {
        this.user = user;
        this.store = store;
        this.orderTypeEnum = orderTypeEnum;
        this.orderPrice = orderPrice;
        this.isCanceled = isCanceled;
        this.orderAddress = orderAddress;
        this.orderRequest = orderRequest;
        this.orderStatusEnum = orderStatusEnum;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.isDeleted = false;
    }

}