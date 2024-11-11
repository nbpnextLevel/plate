package com.sparta.plate.service;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {

        UUID orderId = paymentRequestDto.getOrderId();

        // 1. 주문 ID 가져오기
        Order order = orderRepository.findById(orderId).orElseThrow(()->
                new OrderNotFoundException("Order not found orderId : " + orderId));

        // 2. 결제 객체 생성
        Payment payment = new Payment(paymentRequestDto, order);

        // 4.DB에 저장
        paymentRepository.save(payment);

        // 5. response 생성 및 반환
        return new PaymentResponseDto(payment);
    }


}