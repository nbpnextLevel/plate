package com.sparta.plate.entity;

import com.sparta.plate.dto.request.ReviewRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "p_review")
@NoArgsConstructor
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID reviewId = UUID.randomUUID();

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(length = 2000)
    private String reviewDetail;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int reviewScore;

    @Column
    private boolean reviewStatus = false;

    public Review(ReviewRequestDto reviewRequestDto, Payment payment) {
        this.reviewDetail = reviewRequestDto.getReviewDetail();
        this.reviewScore = reviewRequestDto.getReviewScore();
        this.payment = payment;
        this.reviewStatus = reviewRequestDto.isReviewStatus();
    }

    @PrePersist  // 엔티티가 저장되기 전에 UUID 자동 생성
    public void prePersist() {
        if (reviewId == null) {
            reviewId = UUID.randomUUID();
        }
    }
}