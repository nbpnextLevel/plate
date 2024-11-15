package com.sparta.plate.service.review;

import com.sparta.plate.dto.request.ReviewRequestDto;
import com.sparta.plate.dto.response.ReviewResponseDto;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.entity.Review;
import com.sparta.plate.entity.User;
import com.sparta.plate.exception.*;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.repository.ReviewRepository;
import com.sparta.plate.repository.StoreRepository;
import com.sparta.plate.repository.UserRepository;
import com.sparta.plate.security.UserDetailsImpl;
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


    // 리뷰 작성
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto, Long userId) {

        Payment payment = paymentRepository.findByPaymentId(reviewRequestDto.getPaymentId())
                .orElseThrow(()->new NotFoundPaymentException("결제 내역이 존재하지 않습니다."));

        User user = payment.getOrder().getUser();

        if (!user.getId().equals(userId)) {
            throw new UnauthorizedAccessException("이 결제에 대한 리뷰를 작성할 권한이 없습니다.");
        }


        Review review = new Review(reviewRequestDto, payment);

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(ReviewRequestDto reviewRequestDto, Long userId) {

        UUID paymentId = reviewRequestDto.getPaymentId();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundPaymentException("결제 내역이 존재하지 않습니다."));

        Review review = reviewRepository.findByPayment(payment)
                .orElseThrow(() -> new NotFoundReviewException("해당 리뷰를 찾을 수 없습니다."));

        if(!reviewRequestDto.isReviewStatus()){
            throw new NotFoundReviewException("해당 리뷰는 이미 삭제 되었습니다.");
        }

        if (!review.getPayment().getOrder().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("리뷰를 수정할 권한이 없습니다.");
        }

        if (reviewRequestDto.getReviewDetail() != null && !reviewRequestDto.getReviewDetail().isEmpty()) {
            review.setReviewDetail(reviewRequestDto.getReviewDetail());
        }
        if (reviewRequestDto.getReviewScore() != 0) {
            review.setReviewScore(reviewRequestDto.getReviewScore());
        }

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // 리뷰 삭제
    @Transactional
    public ReviewResponseDto deleteReview(UUID reviewId, Long userId) {

        // 결제 ID랑 연관 된 리뷰 찾기
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReviewException("리뷰가 존재하지 않습니다."));

//        // 결제 ID로 결제 정보 찾기
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new NotFoundPaymentException("결제 내역이 존재하지 않습니다."));

        // 이미 삭제된 리뷰인지
        if (!review.isReviewStatus()) {
            throw new IllegalArgumentException("리뷰는 이미 삭제 되었습니다.");
        }else{
            review.setReviewStatus(false);
        }

        // 리뷰 작성자 확인
        if (!review.getPayment().getOrder().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("리뷰를 삭제할 권한이 없습니다.");
        }

        review.markAsDeleted(userId);

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }


    // 리뷰ID로 단건 조회
    public ReviewResponseDto findById(UUID reviewId, UserDetailsImpl userDetails) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReviewException("리뷰가 존재하지 않습니다."));

        // 현재 로그인한 사용자만 자신의 리뷰를 조회할 수 있도록 체크
        if (!review.getPayment().getOrder().getUser().getId().equals(userDetails.getUser().getId())) {
            throw new UnauthorizedAccessException("리뷰를 조회할 권한이 없습니다.");
        }
        return new ReviewResponseDto(review);
    }

    // userId 사용자별 전체 조회
    public Page<ReviewResponseDto> findReviewByUserId(Long userId, Pageable pageable, UserDetailsImpl userDetails) {

        if (!userDetails.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("리뷰를 조회할 권한이 없습니다.");
        }

        // userId가 존지하는지 확인
        if(userRepository.findByIdAndIsDeletedFalse(userId).isEmpty()){
            throw new UserNotFoundException("해당 사용자는 존재하지 않습니다.");
        }

        // 해당 userId의 결제 정보를 페이징하며 조회
        Page<Review> reviewPage = reviewRepository.findReviewByUserId(userId, pageable);

        return reviewPage.map(ReviewResponseDto::new);
    }

    // userId로 전체 조회 + search
    public Page<ReviewResponseDto> searchReviewByUserIdAndStoreName(Long userId, String storeName,
                                                         Pageable pageable,
                                                         UserDetailsImpl userDetails) {

        if (!userDetails.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("리뷰를 조회할 권한이 없습니다.");
        }

        if(storeName == null || storeName.isEmpty()){
            Page<Review> reviewPage = reviewRepository.findReviewByUserId(userId, pageable);
            return reviewPage.map(ReviewResponseDto::new);
        } else{
            Page<Review> reviewPage = reviewRepository.findByPaymentOrderUserIdAndStoreName(userId, storeName, pageable);
            return reviewPage.map(ReviewResponseDto::new);
        }
    }

    // 가게id로 조회, 모든 권한
    public Page<ReviewResponseDto> findByStoreId(UUID storeId, Pageable pageable) {
        if(storeRepository.findById(storeId).isEmpty()){
            throw new StoreNotFoundException("일치하는 가게명이 없습니다.");
        }

        Page<Review> reviewPage = reviewRepository.findByStoreId(storeId, pageable);

        return reviewPage.map(ReviewResponseDto::new);
    }

}
