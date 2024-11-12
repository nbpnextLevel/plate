package com.sparta.plate.entity;

import com.sparta.plate.dto.request.OrderRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order")  // 테이블 이름 설정
@Getter
@Setter
@NoArgsConstructor
public class Order extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", columnDefinition = "UUID")
    private UUID orderId;  // 주문 ID (UUID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // 외래 키 설정 (user_id)
    private User user;  // 주문한 사용자 (User 엔티티와 연관)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")  // 외래 키 설정 (store_id)
    private Store store;  // 주문이 발생한 상점 (Store 엔티티와 연관)

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderTypeEnum orderType;  // 주문 유형 (ENUM)

    @Column(name = "order_price")
    private Long orderPrice;  // 주문 금액 (NUMERIC(10,0))

    @Column(name = "is_canceled")
    private Boolean isCanceled;  // 주문 취소 여부 (BOOLEAN)

    @Column(name = "order_address", length = 225)
    private String orderAddress;  // 배송지 주소 (VARCHAR(225))

    @Column(name = "order_request", length = 500)
    private String orderRequest;  // 주문 요청 사항 (VARCHAR(500))

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatusEnum orderStatus;  // 주문 상태 (ENUM)

    @Column(name = "is_deleted")
    private Boolean isDeleted;  // 삭제 여부 (BOOLEAN)

    @OneToMany(mappedBy = "order")  // Order 엔티티에서 'product' 필드를 관리
    private List<OrderProduct> orderProductList = new ArrayList<>();


    // 기본 생성자와 매개변수화된 생성자 (선택사항)
    public Order(OrderRequestDto requestDto, User user, Store store, List<OrderProduct> orderProductList) {
        this.user = user;
        this.store = store;
        this.orderType = requestDto.getOrderType();
        this.orderPrice = requestDto.getOrderPrice();
        this.isCanceled = requestDto.getIsCanceled();
        this.orderAddress = requestDto.getOrderAddress();
        this.orderRequest = requestDto.getOrderRequest();
        this.orderStatus= requestDto.getOrderStatus();
        this.orderProductList = orderProductList;
    }


    // payment Test용
    public Order(UUID orderId, User user, Store store, OrderTypeEnum orderType, Long orderPrice, Boolean isCanceled, String orderAddress, String orderRequest, OrderStatusEnum orderStatus, Boolean isDeleted, List<OrderProduct> orderProductList) {
//      this.orderId = orderId;
        this.orderId = orderId;
        this.user = user;
        this.store = store;
        this.orderType = orderType;
        this.orderPrice = orderPrice;
        this.isCanceled = isCanceled;
        this.orderAddress = orderAddress;
        this.orderRequest = orderRequest;
        this.orderStatus = orderStatus;
        this.isDeleted = isDeleted;
        this.orderProductList = orderProductList;
    }

    // payment TEST용
    public Order(UUID uuid, Object o, Object o1, Object o2, long l, boolean b, String 서울, String orderRequest, Object o3, Object o4) {
        super();
    }

    public void addOrderProduct(OrderProduct orderProduct) {
        orderProductList.add(orderProduct);
        orderProduct.changeOrder(this);
    }

    public void changeStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * Order 생성
     */
    private Order(User user) {
        this.user = user;
    }

    public static Order createOrder(User user, OrderProduct... orderProducts) {
        Order order = new Order(user);
        Arrays.stream(orderProducts).forEach(order::addOrderProduct);
        order.changeStatus(OrderStatusEnum.PENDING_PAYMENT);
        return order;
    }

    /**
     * 주문 취소
     */
    public void cancelOrder() {
        // 배달 중일땐 취소 안되야 할것 같음..(배달관련 추후 추가 고민)
        this.changeStatus(OrderStatusEnum.ORDER_CANCELLED);
        //orderProductList.forEach(OrderProduct::cancel);
    }

    /**
     * 전체 주문 가격
     */
    public int getTotalPrice() {
        return orderProductList.stream().mapToInt(OrderProduct::getTotalPrice).sum();
    }

}