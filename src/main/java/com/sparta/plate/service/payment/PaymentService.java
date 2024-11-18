package com.sparta.plate.service.payment;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
import com.sparta.plate.entity.*;
import com.sparta.plate.exception.*;
import com.sparta.plate.repository.OrderRepository;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.repository.StoreRepository;
import com.sparta.plate.repository.UserRepository;
import com.sparta.plate.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
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

//    @Transactional
//    public PaymentResponseDto getPaymentDetails(UUID orderId) {
//        Payment payment = paymentRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Payment not found"));
//
//        // 지연 로딩된 User 객체 초기화
//        Hibernate.initialize(payment.getOrder().getUser());
//
//        // PaymentResponseDto로 변환하여 반환
//        return new PaymentResponseDto(payment);
//    }

    // 결제 요청(orderId를 받아서)
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto, Long userId) {

        // 주문 존재 여부 확인
        Order order = orderRepository.findByOrderIdAndIsDeletedFalseAndIsCanceledFalse(paymentRequestDto.getOrderId())
                .orElseThrow(()-> new OrderNotFoundException("해당 주문이 존재하지 않습니다.")
        );;

        User user = order.getUser();
        // 주문을 요청한 사용자가 로그인한 사용자가 동일한지 확인
        if(!user.getId().equals(userId)) {
            throw new UserNotAuthorizedException("인증 되지 않은 사용자 입니다.");
        }

        // 결제 객체 생성
        Payment payment = new Payment(order);

        order.setOrderStatus(OrderStatusEnum.DELIVERY_COMPLETED);

        // DB에 저장
        paymentRepository.save(payment);
        orderRepository.save(order);

        return new PaymentResponseDto(order, payment);
    }


    // 결제 단건 조희 : paymentId
    public PaymentResponseDto getPaymentBypaymentId(UUID paymentId, Long userId) {

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(()-> new PaymentNotFoundException("결제 내역이 존재하지 않습니다."));

        if(!payment.getOrder().getUser().getId().equals(userId)){
            throw new UserNotAuthorizedException("인증되지 않은 사용자 입니다.");
        }
        return new PaymentResponseDto(payment);
    }


    // 사용자별 조회 : userId
    public Page<PaymentResponseDto> findPaymentByUserId(Long userId, Pageable pageable, UserDetailsImpl userDetails) {

        if (!userDetails.getUser().getId().equals(userId)) {
            throw new PaymentUserMismatchException("사용자와 결제자가 동일하지 않습니다.");
        }

        // userId가 존재하는지 확인
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserNotAuthorizedException("인증되지 않은 사용자 입니다."));

        // 해당 사용자의 결제 정보를 페이징하여 조회
        Page<Payment> paymentPage = paymentRepository.findPaymentByUserId(userId, pageable);

        // 결제 정보를 PaymentResponseDto로 변환하여 반환
        return paymentPage.map(payment -> new PaymentResponseDto(payment));
    }


    // 사용자별 가게 조회 + search
    // http://localhost:8080/api/payments/search/1234567890123?search=메롱상점&page=0&size=10&sortBy=createdAt&isAcs=true
    public Page<Payment> searchPaymentsByUserIdAndStoreName(Long userId, String storeName,
                                                            Pageable pageable,
                                                            UserDetailsImpl userDetails) {
        if (!userDetails.getUser().getId().equals(userId)) {
            throw new UserNotAuthorizedException("인증되지 않은 사용자 입니다.");
        }

        if (storeName == null || storeName.trim().isEmpty()) {
            return paymentRepository.findPaymentByUserId(userId, pageable);
        } else {
            return paymentRepository.findByOrderUserIdAndStoreName(userId, storeName, pageable);
        }
    }



    // 가게별 조회, 권한 줘야함!
    public Page<PaymentResponseDto> getPaymentsByStoreId(UUID storeId, Pageable pageable, UserDetailsImpl userDetails) {

        // 가게id가 존재하는지
        if (storeRepository.findById(storeId).isEmpty()) {
            throw new StoreNotFoundException("가게는 존재 하지 않습니다.");
        }

        // OWNER가 맞는지
        if (userDetails.getUser().getRole() != UserRoleEnum.OWNER){
            throw new UserNotAuthorizedException("인증되지 않은 사용자 입니다.");
        }

        // 가게id의 결제 정보를 페이징 처리
        Page<Payment> paymentPage = paymentRepository.findALLByOrderStoreId(storeId, pageable);

        // return
        return paymentPage.map(PaymentResponseDto::new);
    }
}
