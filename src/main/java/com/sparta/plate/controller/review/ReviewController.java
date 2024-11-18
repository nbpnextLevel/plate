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
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.Map;
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
    @Operation(summary = "리뷰 작성", description = "결제 후, 결제 아이디로 리뷰 작성")
    public ApiResponseDto<Map<String, Object>> createReview(@PathVariable("paymentId") UUID paymentId,
                                                            Long userId,
                                                            @RequestBody ReviewRequestDto reviewRequestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        userId = userDetails.getUser().getId();  // 로그인한 사용자 ID
        ReviewResponseDto reviewResponseDto = reviewService.createReview(reviewRequestDto, userId);
        String successMessage = "리뷰가 성공적으로 생성되었습니다.";

        return ApiResponseDto.success(Map.of(successMessage, reviewResponseDto));
    }

    // 리뷰 수정
    @PatchMapping("/{reviewId}/update")
    @Operation(summary = "리뷰 수정", description = "리뷰 작성 후, 생성된 리뷰 아이디로 리뷰 수정")
    public ApiResponseDto<Map<String, Object>> updateReview(@PathVariable("reviewId") UUID reviewId,
                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        ReviewResponseDto reviewResponseDto = reviewService.updateReview(reviewRequestDto, userId);

        String successMessage = "리뷰가 성공적으로 수정되었습니다.";

        return ApiResponseDto.success(Map.of("message", successMessage, "review", reviewResponseDto));
    }

    // 리뷰 삭제
    @PatchMapping("/{reviewId}/delete")
    @Operation(summary = "리뷰 삭제", description = "리뷰 작성 후, 생성된 리뷰 아이디로 리뷰 삭제")
    public ApiResponseDto<Map<String, Object>> deleteReview(@PathVariable("reviewId") UUID reviewId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        ReviewResponseDto reviewResponseDto = reviewService.deleteReview(reviewId, userId);

        String successMessage = "리뷰가 성공적으로 삭제되었습니다.";

        return ApiResponseDto.success(Map.of("message", successMessage, "review", reviewResponseDto));
    }

    // 리뷰ID로 단건 조회
    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 아이디로 단건 조회", description = "리뷰 작성 후, 생성된 리뷰 아이디로 리뷰 단건 조회")
    public ApiResponseDto<Map<String, Object>> findById(@PathVariable("reviewId") UUID reviewId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ReviewResponseDto reviewResponseDto = reviewService.findById(reviewId, userDetails);

        return ApiResponseDto.success(Map.of("review", reviewResponseDto));
    }

    // userId 사용자별 리뷰 조회
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 리뷰 조회", description = "사용자 아이디로 리뷰 조회")
    public ApiResponseDto<Map<String, Object>> findReviewByUserId(@PathVariable("userId") Long userId,
                                                                                      Pageable pageable,
                                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<ReviewResponseDto> reviewResponseDto = reviewService.findReviewByUserId(userId, pageable, userDetails);

        return ApiResponseDto.success(Map.of("reviews", reviewResponseDto));
    }

    // 사용자별 리뷰 조회 + search
    @Operation(summary = "사용자별 리뷰 검색 조회", description = "사용자 아이디와 가게 이름으로 리뷰 조회")
    @GetMapping("/search/{userId}")
    public ApiResponseDto<Map<String, Object>> findByPaymentOrderUserIdAndStoreName
    (@PathVariable("userId") Long userId,
     @RequestParam(value = "search", required = false) String storeName,
     @RequestParam("page") int page, // 페이지 번호
     @RequestParam("size") int size, // 페이지 사이즈
     @RequestParam("sortBy") String sortBy, // 정렬 기준
     @RequestParam("isAcs") boolean isAcs,
     @AuthenticationPrincipal UserDetailsImpl userDetails){

        Pageable pageable = createPageable(page, size, sortBy, isAcs);
        Page<ReviewResponseDto> reviewPage = reviewService.searchReviewByUserIdAndStoreName(userId, storeName, pageable, userDetails);

        return ApiResponseDto.success(Map.of("reviews", reviewPage));
    }


    // 가게별 리뷰 조회 (모든 권한)
    @GetMapping("/store/{storeId}")
    @Operation(summary = "가게별 리뷰 조회", description = "가게 아이디로 리뷰 조회")
    public ApiResponseDto<Map<String, Object>> findByStoreId(@PathVariable("storeId") UUID storeId, Pageable pageable){
        Page<ReviewResponseDto> reviewPage = reviewService.findByStoreId(storeId, pageable);
        return ApiResponseDto.success(Map.of("reviews", reviewPage));
    }




    private Pageable createPageable(int page, int size, String sortBy, boolean isAcs) {
        // 정렬 방향 설정 (오름차순/내림차순)
        Sort.Direction direction = isAcs ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Sort 객체를 만들어서 Pageable에 전달
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}
