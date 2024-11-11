package com.sparta.plate.controller.payment;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
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

    // userId 사용자별 조회
    @GetMapping("/user/{userId}")
    public Page<PaymentResponseDto> findPaymentByUserId(@PathVariable("userId") Long userId, Pageable pageable) {

        return paymentService.findPaymentByUserId(userId, pageable);
    }

    // userId 조회 + search(storeName)
    // search : 스토어 이름을 검색해서, 해당되는 스토어만 조회
    @GetMapping("/search/{userId}")
    public Page<PaymentResponseDto> searchPaymentsByUserIdAndStoreName(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "search", required = false) String storeName,
            @RequestParam("page") int page, // 페이지 번호
            @RequestParam("size") int size, // 페이지 사이즈
            @RequestParam("sortBy") String sortBy, // 정렬 기준
            @RequestParam("isAcs") boolean isAcs){

        Pageable pageable = createPageable(page, size, sortBy, isAcs);

        // paymentService에서 데이터를 조회
        Page<Payment> paymentPage = paymentService.searchPaymentsByUserIdAndStoreName(userId, storeName, pageable);

        Page<PaymentResponseDto> responseDtoPage = paymentPage.map(PaymentResponseDto::new);

        return responseDtoPage;
    }

    // storeID 조회
    // 가게 사장님이, 결제된 내역들을 조회하기 위해!
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
