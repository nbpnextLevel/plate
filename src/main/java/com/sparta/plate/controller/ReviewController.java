package com.sparta.plate.controller;

import com.sparta.plate.dto.request.ReviewRequestDto;
import com.sparta.plate.dto.response.ReviewResponseDto;
import com.sparta.plate.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;


    // 리뷰 작성
    @PostMapping("/{paymentId}")
    public ReviewResponseDto createReview(@PathVariable("paymentId") UUID paymentId,
                                          @RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.createReview(reviewRequestDto);
    }

    // 리뷰 수정
    @PutMapping("/{paymentId}")
    public ReviewResponseDto updateReview(@PathVariable UUID paymentId,
                                          @RequestBody ReviewRequestDto reviewRequestDto) {

        reviewRequestDto.setPaymentId(paymentId);
        return reviewService.updateReview(reviewRequestDto);
    }

    // 리뷰 삭제
    @PutMapping("/{paymentId}/delete")
    public ReviewResponseDto deleteReview(@PathVariable UUID paymentId) {
        return reviewService.deleteReview(paymentId);
    }

}
