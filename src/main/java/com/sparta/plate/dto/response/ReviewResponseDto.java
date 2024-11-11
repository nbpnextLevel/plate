package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Review;
import com.sparta.plate.entity.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ReviewResponseDto {
    private UUID paymentId;
    private UUID reviewId;
    private Long userId;
    private String reviewDetail;
    private int reviewScore;
    private boolean reviewStatus;

    private Long createBy;
    private LocalDateTime createAt;
    private Long updateBy;


    private LocalDateTime updateAt;

    private Long deletedBy;

    private LocalDateTime deletedAt;

    public ReviewResponseDto(Review review) {
        this.paymentId = review.getPayment().getPaymentId();
        this.reviewId = review.getReviewId();
        this.userId = review.getPayment().getOrder().getUser().getId();
        this.reviewDetail = review.getReviewDetail();
        this.reviewScore = review.getReviewScore();
        this.reviewStatus = review.isReviewStatus();

        this.createBy = review.getPayment().getOrder().getUser().getId();
        this.createAt = review.getCreatedAt();
        this.updateBy = this.createBy; // 생성자랑 동일
        this.updateAt = review.getUpdateAt();
        this.deletedBy = getDeletedBy();
        this.deletedAt = review.getDeletedAt();
    }
}
