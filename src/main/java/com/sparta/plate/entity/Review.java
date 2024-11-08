package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "p_review")
@NoArgsConstructor
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;  // 리뷰 아이디 (PK)

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)  // 주문 아이디 (FK)
    private Order orderId;  // 리뷰가 속한 주문

    @Column(length = 1000)
    private String reviewDetail;  // 리뷰 내용

    @Column()
    private int reviewScore;  // 별점

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewEnum reviewEnum;  // 리뷰 상태 (ACTIVE, DELETED)

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 생성일

    @Column(nullable = false)
    private String createdBy;  // 생성자

    private LocalDateTime updatedAt;  // 수정일

    private String updatedBy;  // 수정자

    private LocalDateTime deletedAt;  // 삭제일

    private String deletedBy;  // 삭제자

    @Column(nullable = false)
    private boolean deleted;  // 삭제 여부
}