package com.sparta.plate.service.review;

import com.sparta.plate.dto.request.ReviewRequestDto;
import com.sparta.plate.dto.response.ReviewResponseDto;
import com.sparta.plate.entity.*;
import com.sparta.plate.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReviewServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private StoreRepository storeRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewService reviewService;

    private User customer;
    private User owner;
    private Store store;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {

        customer = User.builder()
                .loginId("testId")
                .password("testpw")
                .nickname("테스트닉네임")
                .role(UserRoleEnum.CUSTOMER)
                .email("test@test.test")
                .phone("010-0000-0000")
                .address("테스트시 테스트구 테스트동")
                .build();

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> {
                    User savedUser = invocation.getArgument(0);  // 저장된 user 객체
                    // Mock으로 id를 설정
                    Field idField = User.class.getDeclaredField("id");
                    idField.setAccessible(true); // id 필드 접근 가능하도록 설정
                    idField.set(savedUser, 1L); // mock으로 id를 1L로 설정
                    return savedUser;
                });

        userRepository.save(customer);
        owner = User.builder()
                .loginId("yummyId")
                .password("testpw")
                .nickname("야미닉네임")
                .role(UserRoleEnum.OWNER)
                .email("yummy@test.test")
                .phone("010-1111-1111")
                .address("야미시 야미구 야미동")
                .build();

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> {
                    User savedUser = invocation.getArgument(0);  // 저장된 user 객체
                    // Mock으로 id를 설정
                    Field idField = User.class.getDeclaredField("id");
                    idField.setAccessible(true); // id 필드 접근 가능하도록 설정
                    idField.set(savedUser, 2L); // mock으로 id를 1L로 설정
                    return savedUser;
                });

        StoreCategory category = new StoreCategory(UUID.randomUUID(), "냠냠");

        store = Store.builder()
                .user(owner)
                .storeCategory(category)
                .storeName("테스트 가게")
                .storeNumber("02-222-2222")
                .address("맛있시 맛있구 맛있동")
                .build();
        storeRepository.save(store);

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("햄버거")
                .description("참깨빵 위에 순쇠고기 패티 두장 특별한 소스 양상추 치즈 피클 양파까아지 빠라 빠빠빰!")
                .price(new BigDecimal("8000"))
                .displayStatus(ProductDisplayStatusEnum.PENDING_SALE)
                .maxOrderLimit(100)
                .stockQuantity(20)
                .isHidden(false)
                .build();
        productRepository.save(product);


        order = new Order();
        order.setUser(customer);
        order.setOrderId(UUID.randomUUID());
        order.setOrderType(OrderTypeEnum.DELIVERY);
        order.setOrderPrice(1000L);
        order.setIsCanceled(false);
        order.setOrderAddress(customer.getAddress());
        order.setOrderRequest("맛있게 부탁드려용");
        order.setOrderStatus(OrderStatusEnum.PENDING_PAYMENT);
        order.setIsDeleted(false);
        order.setStore(store);
        order.setOrderProductList(new ArrayList<>(List.of(
                new OrderProduct() {{
                    setOrderProductId(UUID.randomUUID());
                    setProduct(product);
                    setOrderQuantity(1);
                }}, new OrderProduct() {{
                    setOrderProductId(UUID.randomUUID());
                    setProduct(product);
                    setOrderQuantity(2);
                }}
        )));

        // Order를 먼저 저장하여 order_id가 p_order 테이블에 들어가도록 설정
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
        orderRepository.save(order);  // 실제로 order를 저장하고 DB에 기록

        // orderRepository의 findByOrderIdAndIsDeletedFalseAndIsCanceledFalse() 메소드도 mock
        Mockito.when(orderRepository.findByOrderIdAndIsDeletedFalseAndIsCanceledFalse(order.getOrderId()))
                .thenReturn(Optional.of(order));  // 해당 order_id를 가진 주문을 반환

        UUID paymentId = UUID.randomUUID();

        payment = Payment.builder()
                .paymentId(paymentId)
                .paymentNumber("PAY_" + paymentId.toString())
                .order(order)
                .amount(order.getOrderPrice())
                .build();

        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(payment);

        Mockito.when(paymentRepository.findByPaymentId(payment.getPaymentId()))
                .thenReturn(Optional.of(payment));

        paymentRepository.save(payment);
    }



    @Test
    public void createReview() {

        String reviewDetails = "맛있다리우스";
        int reviewScore = 5;

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(customer.getId(), payment.getPaymentId(), reviewDetails, reviewScore, true);

        // when : 리뷰 생성
        ReviewResponseDto reviewResponseDto = reviewService.createReview(reviewRequestDto, customer.getId());

        // then: 결제 성공 여부 확인
        assertNotNull(reviewResponseDto.getReviewId(), "리뷰 생성 실패");
        assertEquals(payment.getPaymentId(), reviewResponseDto.getPaymentId(), "리뷰 요청 된 p_payment p_review paymentId 불일치");
        System.out.println("Review Response DTO: " + reviewResponseDto.getPaymentId());
        System.out.println("Review Response DTO: " + reviewResponseDto.getReviewId());
        System.out.println("Review Response DTO: " + reviewResponseDto.getReviewDetail());
        System.out.println("Review Response DTO: " + reviewResponseDto.getReviewScore());


    }
}