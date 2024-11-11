package com.sparta.plate.controller;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    // 결제 요청
    @PostMapping("/{orderId}")
    public PaymentResponseDto createPayment(@PathVariable("orderId") UUID orderId,
                                            @RequestBody PaymentRequestDto paymentRequestDto) {
        paymentRequestDto.setOrderId(orderId);
        paymentRequestDto.setCanceled(false);

        return paymentService.createPayment(paymentRequestDto);
    }


}
