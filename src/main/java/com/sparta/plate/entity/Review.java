package com.sparta.plate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "p_review")
@NoArgsConstructor
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewId;  // 리뷰 아이디 (PK)

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;  // 주문 아이디 (FK)

    @Column(length = 2000)
    private String reviewDetail;  // 리뷰 내용

    @Column(nullable = false)
    private int reviewScore;  // 별점

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 생성일

    @Column(nullable = false)
    private Long createdBy;  // 생성자

    private LocalDateTime updatedAt;  // 수정일

    private String updatedBy;  // 수정자

    private LocalDateTime deletedAt;  // 삭제일

    private String deletedBy;  // 삭제자

    private boolean isDeleted;  // 삭제 여부
}
