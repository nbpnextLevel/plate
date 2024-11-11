package com.sparta.plate.service.payment;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.Payment;
import com.sparta.plate.repository.OrderRepository;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.repository.StoreRepository;
import com.sparta.plate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;


    // 결제 생성(orderId를 받아서)
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
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


    // 결제 ID로 결제 조회
    public PaymentResponseDto getPaymentBypaymentId(UUID paymentId) {

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(()-> new NullPointerException("Payment not found for paymentId" + paymentId));

        return new PaymentResponseDto(payment);
    }

//    // 로그인 ID로 결제 조회, 페이징 처리
//    public Page<PaymentResponseDto> getPaymentByLoginId(String loginId) {
//
//        if(!userRepository.existsByLoginId(loginId)) {
//            throw new IllegalArgumentException("LoginId not found for loginId" + loginId);
//        }
//
//        Page<Payment> paymentPage = paymentRepository.findByLoginId(loginId);
//
//
//        return paymentPage.map(payment -> new PaymentResponseDto(payment));
//
//    }

    // 로그인 ID로 결제 조회, 페이징 처리
//    public Page<PaymentResponseDto> findPaymentsByUserLoginId(String loginId, Pageable pageable) {
//
//        // 로그인 ID가 존재하는지 확인
//        if (!userRepository.existsByLoginId(loginId)) {
//            throw new IllegalArgumentException("LoginId not found for loginId: " + loginId);
//        }
//
//        // loginId로 해당 사용자의 결제 정보를 페이징하여 조회
//        Page<Payment> paymentPage = paymentRepository.findPaymentsByUserLoginId(loginId, pageable);
//
//        // 결제 정보를 PaymentResponseDto로 변환하여 반환
//        return paymentPage.map(payment -> new PaymentResponseDto(payment));
//    }

    // userId로 전체 조회
    public Page<PaymentResponseDto> findPaymentByUserId(Long userId, Pageable pageable) {

        // userId가 존재하는지 확인
        if (userRepository.findByIdAndIsDeletedFalse(userId).isEmpty()) {
            throw new IllegalArgumentException("userId not found for loginId: " + userId);
        }

        // 해당 사용자의 결제 정보를 페이징하여 조회
        Page<Payment> paymentPage = paymentRepository.findPaymentByUserId(userId, pageable);

        // 결제 정보를 PaymentResponseDto로 변환하여 반환
        return paymentPage.map(payment -> new PaymentResponseDto(payment));
    }


    // userId, 페이지, search로 조회
    // http://localhost:8080/api/payments/search/1234567890123?search=메롱상점&page=0&size=10&sortBy=createdAt&isAcs=true
    public Page<Payment> searchPaymentsByUserIdAndStoreName(Long userId, String storeName, Pageable pageable) {
        if (storeName == null || storeName.trim().isEmpty()) {
            return paymentRepository.findPaymentByUserId(userId, pageable);
        } else {
            return paymentRepository.findByOrderUserIdAndStoreName(userId, storeName, pageable);
        }
    }



    // 가게 id로 조회, 권한 줘야함!
    public Page<PaymentResponseDto> getPaymentsByStoreId(UUID storeId, Pageable pageable) {

        // 1. 가게id가 존재하는지
        if (storeRepository.findById(storeId).isEmpty()) {
            throw new IllegalArgumentException("storeId not found for storeId: " + storeId);
        }

        // 2. 가게id의 결제 정보를 페이징 처리
        Page<Payment> paymentPage = paymentRepository.findALLByOrderStoreId(storeId, pageable);

        // 3. return
        return paymentPage.map(PaymentResponseDto::new);
    }




    // 유저 ID로 결제 조회
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
