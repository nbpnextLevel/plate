package com.sparta.plate.controller.review;

import com.sparta.plate.dto.request.ReviewRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.dto.response.ReviewResponseDto;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.entity.Review;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.payment.PaymentService;
import com.sparta.plate.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewResponseDto reviewResponseDto;
    private final PaymentService paymentService;

//     리뷰 작성
    @PostMapping("/{paymentId}")
    public ReviewResponseDto createReview(@PathVariable("paymentId") UUID paymentId,
                                          @RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.createReview(reviewRequestDto);
    }
//    @PostMapping("/{paymentId}")
//    public ReviewResponseDto createReview(@PathVariable("paymentId") UUID paymentId,
//                                          @RequestBody ReviewRequestDto reviewRequestDto,
//                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        // 1. 로그인한 사용자 확인
//        if(userDetails == null){
//            throw new IllegalArgumentException("User not authenticated");
//        }
//
//        // paymentId 해당하는 결제 정보 조회
//        paymentService.getPaymentBypaymentId(paymentId);
//
//
//
//        return reviewService.createReview(reviewRequestDto);
//    }

    // 리뷰 수정
    @PutMapping("/{paymentId}/update")
    public ReviewResponseDto updateReview(@PathVariable("paymentId") UUID paymentId,
                                          @RequestBody ReviewRequestDto reviewRequestDto) {

        reviewRequestDto.setPaymentId(paymentId);

        return reviewService.updateReview(reviewRequestDto);
    }

    // 리뷰 삭제
    @PutMapping("/{paymentId}/delete")
    public ReviewResponseDto deleteReview(@PathVariable("paymentId") UUID paymentId) {

        //reviewResponseDto.setDeletedBy(); > 현재 로그인한 user의 아이디를

        return reviewService.deleteReview(paymentId);
    }

    // 리뷰 단건 조회
    @GetMapping("/{reviewId}")
    public ReviewResponseDto findById(@PathVariable("reviewId") UUID reviewId){
        return reviewService.findById(reviewId);
    }

    // 사용자별 리뷰 조회
    @GetMapping("/user/{userId}")
    public Page<ReviewResponseDto> findReviewByUserId(@PathVariable("userId") Long userId, Pageable pageable){
        return reviewService.findReviewByUserId(userId, pageable);
    }

    // 사용자별 리뷰 조회 + search
    @GetMapping("/search/{userId}")
    public Page<ReviewResponseDto> findByPaymentOrderUserIdAndStoreName
    (@PathVariable("userId") Long userId,
     @RequestParam(value = "search", required = false) String storeName,
     @RequestParam("page") int page, // 페이지 번호
     @RequestParam("size") int size, // 페이지 사이즈
     @RequestParam("sortBy") String sortBy, // 정렬 기준
     @RequestParam("isAcs") boolean isAcs){

        Pageable pageable = createPageable(page, size, sortBy, isAcs);
        Page<Review> reviewPage = reviewService.searchReviewByUserIdAndStoreName(userId, storeName, pageable);
        Page<ReviewResponseDto> responseDtoPage = reviewPage.map(ReviewResponseDto::new);

        return responseDtoPage;
    }


    // 가게별 리뷰 조회 (모든 권한)
    @GetMapping("/store/{storeId}")
    public Page<ReviewResponseDto> findByStoreId(@PathVariable("storeId") UUID storeId, Pageable pageable){
        return reviewService.findByStoreId(storeId, pageable);
    }





    private Pageable createPageable(int page, int size, String sortBy, boolean isAcs) {
        // 정렬 방향 설정 (오름차순/내림차순)
        Sort.Direction direction = isAcs ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Sort 객체를 만들어서 Pageable에 전달
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}
