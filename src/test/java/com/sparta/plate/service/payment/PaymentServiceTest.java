package com.sparta.plate.service.payment;

import com.sparta.plate.entity.*;
import com.sparta.plate.repository.OrderRepository;
import com.sparta.plate.repository.PaymentRepository;
import com.sparta.plate.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserDetailsImpl userDetails;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private User user_OWNER;
    private Store store;
    private StoreCategory store_category;
    private Order order;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .loginId("testId")
                .password("testpw")
                .nickname("테스트닉네임")
                .role(UserRoleEnum.CUSTOMER)
                .email("test@test.test")
                .phone("010-0000-0000")
                .address("테스트시 테스트구 테스트동")
                .build();

        user_OWNER = User.builder()
                .loginId("yummyId")
                .password("testpw")
                .nickname("야미닉네임")
                .role(UserRoleEnum.OWNER)
                .email("yummy@test.test")
                .phone("010-1111-1111")
                .address("야미시 야미구 야미동")
                .build();

        store_category = StoreCategory.builder()
                .id(UUID.randomUUID())
                .category("한식")
                .build();

        store = Store.builder()
                .user(user_OWNER)
                .storeCategory(store_category)
                .storeName("야미야미")
                .storeNumber("02-222-2222")
                .address("맛있시 맛있구 맛있동")
                .build();

        order.setOrderId(UUID.randomUUID());
        order.setOrderType(OrderTypeEnum.DELIVERY);
        order.setOrderPrice(1000L);
        order.setIsCanceled(false);
        order.setOrderAddress(user.getAddress());
        order.setOrderRequest("맛있게 부탁드려용");
        order.setOrderStatus(OrderStatusEnum.PENDING_PAYMENT);
        order.setIsDeleted(false);
        order.setOrderProductList();
    }

    @Test
    void createPayment() {
    }
}