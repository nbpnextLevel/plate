package com.sparta.plate.service.payment;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.repository.OrderRepository;
import com.sparta.plate.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        // TEST용, paymentId를 넣어줌
        // Order order = new Order(UUID.fromString("90c0fa21-5fe3-43cd-b268-84ba4b52d139"), null, null, null, 1000L, false, "서울", "orderRequest", null, null);

        Order order = orderRepository.findById(paymentRequestDto.getOrderId()).orElseThrow(()->
                new NullPointerException("orderId 존재하지 않음")
        );;

        // 결제 객체 생성
        Payment payment = new Payment(order);

        // DB에 저장
        paymentRepository.save(payment);

        // 5. response 생성 및 반환
        return new PaymentResponseDto(order, payment);
    }





//    // 결제 ID로 결제 조회
//    public PaymentResponseDto getPaymentBypaymentId(UUID paymentId) {
//
//        Payment payment = paymentRepository.findByPaymentId(paymentId)
//                .orElseThrow(()-> new PaymentNotFoundException("Payment not found for paymentId" + paymentId));
//
//        return new PaymentResponseDto(payment);
//    }
//
//    // 유저 ID로 결제 조회
//    public PaymentResponseDto getPaymentByUserId(
//                                UUID userId, int page,
//                                int size, String sortBy,
//                                boolean isAse, String search) {
//
//        // 1. userId로 사용자의 주문을 찾는다
//        List<Order> orderList = orderRepository.findByUserId(userId);
//
//        // 2. 주문 리스트에서 결제 정보를 가져온다.
//
//        return new PaymentResponseDto(payment);
//    }

}