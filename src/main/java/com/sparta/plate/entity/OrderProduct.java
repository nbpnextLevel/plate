package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import static com.sparta.plate.entity.ProductDisplayStatusEnum.IN_STOCK;

@Entity
@Table(name = "p_order_product")  // 테이블 이름 설정
@Getter
@Setter
@NoArgsConstructor
public class OrderProduct {

    @Id
    @Column(name = "order_product_id", updatable = false, nullable = false)
    private UUID orderProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)  // 외래 키 설정 (user_id)
    private Order order; // 주문 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)  // 외래 키 설정 (user_id)
    private Product product; // 상품 ID

    @Column(name = "product_history_id", nullable = false)
    private UUID productHistoryId; //

    @Column(nullable = false)
    private int orderQuantity; // 주문 수량


    // 상품의 총 가격 계산 (수량 * 상품 가격)
    public long getTotalPrice() {
        return this.product.getPrice().longValue() * this.orderQuantity;
    }

    public boolean getOrderLimit(){
        return this.product.getMaxOrderLimit() < this.orderQuantity;
    }

    public boolean getOrderQuantityLimit(){
        return this.product.getStockQuantity() < this.orderQuantity;
    }
    public boolean getProductDisplayStatus(){
        return !this.product.getDisplayStatus().equals(IN_STOCK);
    }

    public void setProductStockQuantity() {
        this.product.setStockQuantity(this.product.getStockQuantity() - this.orderQuantity);
    }

    public void resetProductStockQuantity() {
        this.product.setStockQuantity(this.product.getStockQuantity() + this.orderQuantity);
    }


}