package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Review;
import com.sparta.plate.entity.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private UUID paymentId;
    private UUID reviewId;
    private String reviewDetail;
    private int reviewScore;
    private boolean reviewStatus;

    private Timestamped timestamped;

    public ReviewResponseDto(Review review) {
        this.paymentId = review.getPayment().getPaymentId();
        this.reviewId = review.getReviewId();
        this.reviewDetail = review.getReviewDetail();
        this.reviewScore = review.getReviewScore();
        this.reviewStatus = review.isReviewStatus();
    }
}