package com.sparta.plate.service.review;

import com.sparta.plate.dto.request.ReviewRequestDto;
import com.sparta.plate.dto.response.ReviewResponseDto;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.entity.Review;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.repository.ReviewRepository;
import com.sparta.plate.repository.StoreRepository;
import com.sparta.plate.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;


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

        // 3. reviewStatus == false인 경우 예외처리
        if (!review.isReviewStatus()) {
            throw new IllegalArgumentException("Review is already deleted for paymentId: " + paymentId);
        }else{
            review.setReviewStatus(false);
        }


        // Long 타입의 userId가 들어가야함
        // review > payment > order > user > userId
        review.markAsDeleted(payment.getOrder().getUser().getId());

        // 수정된 리뷰 저장
        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }


    // 리뷰ID로 단건 조회
    public ReviewResponseDto findById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found for reviewId: " + reviewId));

        return new ReviewResponseDto(review);
    }

    // userId로 전체 조회
    public Page<ReviewResponseDto> findReviewByUserId(Long userId, Pageable pageable) {

        // userId가 존지하는지 확인
        if(userRepository.findByIdAndIsDeletedFalse(userId).isEmpty()){
            throw new IllegalArgumentException("user not found");
        }

        // 해당 userId의 결제 정보를 페이징하며 조회
        Page<Review> reviewPage = reviewRepository.findReviewByUserId(userId, pageable);

        return reviewPage.map(ReviewResponseDto::new);
//        return reviewPage.map(review -> new ReviewResponseDto(review));
    }

    // userId로 전체 조회 + search
    public Page<Review> searchReviewByUserIdAndStoreName(Long userId, String storeName,Pageable pageable) {
        if(storeName == null || storeName.isEmpty()){
            return reviewRepository.findReviewByUserId(userId, pageable);
        } else{
            return reviewRepository.findByPaymentOrderUserIdAndStoreName(userId, storeName, pageable);
        }
    }

    // 가게id로 조회, 모든 권한
    public Page<ReviewResponseDto> findByStoreId(UUID storeId, Pageable pageable) {
        if(storeRepository.findById(storeId).isEmpty()){
            throw new IllegalArgumentException("store not found");
        }

        Page<Review> reviewPage = reviewRepository.findByStoreId(storeId, pageable);

        return reviewPage.map(ReviewResponseDto::new);
    }

}
