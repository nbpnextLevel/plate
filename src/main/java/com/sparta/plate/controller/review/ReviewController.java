package com.sparta.plate.controller.review;

import com.sparta.plate.dto.request.ReviewRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
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
import org.springframework.http.ResponseEntity;
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

    //리뷰 작성
    @PostMapping("/{paymentId}")
    public ResponseEntity<ApiResponseDto<ReviewResponseDto>> createReview(@PathVariable("paymentId") UUID paymentId,
                                                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();  // 로그인한 사용자 ID

        // 리뷰 서비스 호출
        ReviewResponseDto reviewResponseDto = reviewService.createReview(reviewRequestDto, userId);

        return ResponseEntity.ok(ApiResponseDto.success("리뷰가 성공적으로 생성되었습니다.", reviewResponseDto));
    }

    // 리뷰 수정
    @PutMapping("/{paymentId}/update")
    public ResponseEntity<ApiResponseDto<ReviewResponseDto>> updateReview(@PathVariable("paymentId") UUID paymentId,
                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        ReviewResponseDto reviewResponseDto = reviewService.updateReview(reviewRequestDto, userId);

        return ResponseEntity.ok(ApiResponseDto.success("리뷰가 성공적으로 수정되었습니다.", reviewResponseDto));
    }

    // 리뷰 삭제
    @PutMapping("/{paymentId}/delete")
    public ResponseEntity<ApiResponseDto<ReviewResponseDto>> deleteReview(@PathVariable("paymentId") UUID paymentId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        ReviewResponseDto reviewResponseDto = reviewService.deleteReview(paymentId, userId);

        return ResponseEntity.ok(ApiResponseDto.success("리뷰가 성공적으로 삭제되었습니다.", reviewResponseDto));
    }

    // 리뷰ID로 단건 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponseDto<ReviewResponseDto>> findById(@PathVariable("reviewId") UUID reviewId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ReviewResponseDto reviewResponseDto = reviewService.findById(reviewId, userDetails);
        return ResponseEntity.ok(ApiResponseDto.success(reviewResponseDto));
    }

    // userId 사용자별 리뷰 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDto<Page<ReviewResponseDto>>> findReviewByUserId(@PathVariable("userId") Long userId,
                                                                                      Pageable pageable,
                                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<ReviewResponseDto> reviewResponseDto = reviewService.findReviewByUserId(userId, pageable, userDetails);

        return ResponseEntity.ok(ApiResponseDto.success(reviewResponseDto));
    }

    // 사용자별 리뷰 조회 + search
    @GetMapping("/search/{userId}")
    public Page<ReviewResponseDto> findByPaymentOrderUserIdAndStoreName
    (@PathVariable("userId") Long userId,
     @RequestParam(value = "search", required = false) String storeName,
     @RequestParam("page") int page, // 페이지 번호
     @RequestParam("size") int size, // 페이지 사이즈
     @RequestParam("sortBy") String sortBy, // 정렬 기준
     @RequestParam("isAcs") boolean isAcs,
     @AuthenticationPrincipal UserDetailsImpl userDetails){

        Pageable pageable = createPageable(page, size, sortBy, isAcs);

        Page<ReviewResponseDto> reviewPage = reviewService.searchReviewByUserIdAndStoreName(userId, storeName, pageable, userDetails);
        return reviewPage;
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
