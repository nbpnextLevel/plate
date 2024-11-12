package com.sparta.plate.controller;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    // 결제 요청
    @PostMapping("/{orderId}")
    public PaymentResponseDto createPayment(@PathVariable("orderId") UUID orderId,
                                            @RequestBody PaymentRequestDto paymentRequestDto) {

        return paymentService.createPayment(paymentRequestDto);
    }

    // 결제 단건 조회
    @GetMapping("/{paymentId}")
    public PaymentResponseDto getPaymentBypaymentId(@PathVariable("paymentId") UUID paymentId) {
        return paymentService.getPaymentBypaymentId(paymentId);
    }

//    @GetMapping("/loginId")
//    public Page<PaymentResponseDto> getPaymentsByLoginId(@RequestParam("loginId") String loginId, Pageable pageable) {
//        return paymentService.getPaymentsByLoginId(loginId,pageable);
//    }

    // loginId 조회
    @GetMapping("/user/{loginId}")
    public Page<PaymentResponseDto> findByOrderUserLoginId(@PathVariable("loginId") String loginId, Pageable pageable) {

        return paymentService.getPaymentsByLoginId(loginId, pageable);
    }

    // ERROR loginId조회 + search(storeName)
    @GetMapping("/search/{loginId}")
    public Page<PaymentResponseDto> getPaymentsByLoginIdAndSearch(
            @PathVariable("loginId") String loginId,
            @RequestParam(value = "search", required = false) String storeName,
            @RequestParam("page") int page, // 페이지 번호
            @RequestParam("size") int size, // 페이지 사이즈
            @RequestParam("sortBy") String sortBy, // 정렬 기준
            @RequestParam("isAcs") boolean isAcs){

        Pageable pageable = createPageable(page, size, sortBy, isAcs);

        return paymentService.getPaymentsByLoginIdAndSearch(loginId,storeName,pageable);
    }

    // ERROR storeId 조회, 1개밖에 안 나옴ㅠㅠ
    @GetMapping("/store/{storeId}")
    public Page<PaymentResponseDto> getPaymentsByStoreId(@PathVariable("storeId") UUID storeId, Pageable pageable) {
        return paymentService.getPaymentsByStoreId(storeId, pageable);
    }










    private Pageable createPageable(int page, int size, String sortBy, boolean isAcs) {
        // 정렬 방향 설정 (오름차순/내림차순)
        Sort.Direction direction = isAcs ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Sort 객체를 만들어서 Pageable에 전달
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

}
