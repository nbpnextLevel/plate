package com.sparta.plate.service.review;

import com.sparta.plate.dto.request.ReviewRequestDto;
import com.sparta.plate.dto.response.ReviewResponseDto;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.entity.Review;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;


    // 리뷰 생성
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto) {

        Payment payment = paymentRepository.findByPaymentId(reviewRequestDto.getPaymentId())
                .orElseThrow(()->new NullPointerException("paymentId 존재하지 않음"));

        Review review = new Review(reviewRequestDto, payment);

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(ReviewRequestDto reviewRequestDto) {

        // 1. paymentId로 결제 정보 찾기
        UUID paymentId = reviewRequestDto.getPaymentId();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for paymentId: " + paymentId));

        // 2. paymentId로 리뷰 찾기
        Review review = reviewRepository.findByPayment(payment)
                .orElseThrow(() -> new IllegalArgumentException("Review not found for paymentId: " + paymentId));

        // 3. 리뷰 수정
        if (reviewRequestDto.getReviewDetail() != null && !reviewRequestDto.getReviewDetail().isEmpty()) {
            review.setReviewDetail(reviewRequestDto.getReviewDetail());
        }
        if (reviewRequestDto.getReviewScore() != 0) {
            review.setReviewScore(reviewRequestDto.getReviewScore());
        }

        // 4. 리뷰 저장!
        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // 리뷰 삭제
    @Transactional
    public ReviewResponseDto deleteReview(UUID paymentId) {

        // 1. 결제 ID로 결제 정보 찾기
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for paymentId: " + paymentId));

        // 2. 결제 ID랑 연관 된 리뷰 찾기
        Review review = reviewRepository.findByPayment(payment)
                .orElseThrow(() -> new IllegalArgumentException("Review not found for paymentId: " + paymentId));

        // 3. reviewStatus == true인 경우 예외처리
        if (review.isReviewStatus()) {
            throw new IllegalArgumentException("Review is already deleted for paymentId: " + paymentId);
        }else{

            review.setReviewStatus(true);
        }

        // 수정된 리뷰 저장
        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

}
