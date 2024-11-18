package com.sparta.plate.service.payment;

import com.sparta.plate.dto.request.PaymentRequestDto;
import com.sparta.plate.dto.response.PaymentResponseDto;
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
public class PaymentServiceTest {

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
    private PaymentService paymentService;

    private User customer;
    private User owner;
    private Store store;
    private Order order;



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
    }



    @Test
    public void createPayment() {

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(
                customer.getId(),
                order.getOrderId(),
                order.getOrderPrice(),
                order.getIsCanceled()
        );


        // when : 주문 처리 및 결제 생성
        PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto, customer.getId());

        // then: 결제 성공 여부 확인
        assertTrue(paymentResponseDto.getIsPaid(), "결제 요청 실패");
        assertNotNull(paymentResponseDto.getOrderId());
        assertEquals(order.getOrderId(), paymentResponseDto.getOrderId(), "결제 요청 된 p_order와 p_payment의 orderId 불일치");

        System.out.println(paymentResponseDto.getOrderId());
        System.out.println(order.getOrderId());

    }
}