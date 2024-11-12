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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private StoreRepository storeRepository;



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
    public Page<PaymentResponseDto> getPaymentsByLoginId(String loginId, Pageable pageable) {

        // 로그인 ID가 존재하는지 확인
        if (!userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("LoginId not found for loginId: " + loginId);
        }

        // loginId로 해당 사용자의 결제 정보를 페이징하여 조회
        Page<Payment> paymentPage = paymentRepository.findByOrderUserLoginId(loginId, pageable);

        // 결제 정보를 PaymentResponseDto로 변환하여 반환
        return paymentPage.map(payment -> new PaymentResponseDto(payment));
    }

    public Page<PaymentResponseDto> getPaymentsByLoginIdAndSearch(String loginId, String storeName, Pageable pageable) {

        Page<Payment> paymentPage;

        System.out.println(loginId);
        System.out.println(storeName);

        if(storeName.trim().isEmpty()) {
            System.out.println("여기에 들어가면 오류 입니다");
            paymentPage = paymentRepository.findByOrderUserLoginId(loginId, pageable);
        }else{
            System.out.println("여기에 들어가면 맞습니다.");
            paymentPage = paymentRepository.searchPaymentsByLoginIdAndStoreName(loginId, storeName, pageable);
        }


        // 결제 정보를 PaymentResponseDto로 변환하여 반환
        return paymentPage.map(PaymentResponseDto::new);
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