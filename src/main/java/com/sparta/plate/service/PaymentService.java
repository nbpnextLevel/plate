package com.sparta.plate.service;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

//    @Autowired
//    private OrderRepository orderRepository;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {

        // 가게
       // 주문 > 주문ID 생성 > 주문 ㅇㅋ
       // >> 결제
       // 결제ID 생성
       // 결제요청dto : 주문ID, 결제금액, 주문이 취소됐는지 안 됐는지
        // 결제응답dto : 결제ID, 주문ID, 결제금액, 가게정보들

        //Order order = orderRepository.findById(paymentRequestDto.getOrderId());


        // 주문이 5분 내로 취소가 가능,
        // isCanceled == false 결제x > 예외처리
        //order.getIsCanceled()


        // TEST용
        Order order = new Order(UUID.fromString("90c0fa21-5fe3-43cd-b268-84ba4b52d139"), null, null, null, 1000L, false, "서울", "orderRequest", null, null);

        // 2. 결제 객체 생성
        Payment payment = new Payment(order);

        System.out.println(payment.getPaymentId());
        System.out.println(payment.getAmount());

        // 4.DB에 저장
        paymentRepository.save(payment);



        // 5. response 생성 및 반환
        return new PaymentResponseDto(payment);
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