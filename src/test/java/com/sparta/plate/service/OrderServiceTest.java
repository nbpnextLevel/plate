package com.sparta.plate.service;

import com.sparta.plate.dto.request.OrderProductRequestDto;
import com.sparta.plate.dto.request.OrderRequestDto;
import com.sparta.plate.entity.*;
import com.sparta.plate.exception.InvalidDisplayStatusException;
import com.sparta.plate.exception.OrderQuantityExceededException;
import com.sparta.plate.repository.*;
import com.sparta.plate.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductHistoryRepository productHistoryRepository;

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StoreCategoryRepository storeCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private User customer, owner1;
    private Store store;
    private Product product1, product2;
    private ProductHistory proHis1,proHis2;


    @BeforeEach
    void setUp() {
        // 사용자 객체 생성
        customer = User.builder()
                .loginId("customer12")
                .password("Customer1234")
                .nickname("고객1")
                .role(UserRoleEnum.CUSTOMER)
                .email("customer1@naver.com")
                .phone("010-1234-5678")
                .address("경기도 성남시 분당구 네이버로23")
                .build();

        customer = userRepository.save(customer);

        owner1 = User.builder()
                .loginId("owner1")
                .password("Owner1234")
                .nickname("주인1")
                .role(UserRoleEnum.OWNER)
                .email("owner1@naver.com")
                .phone("010-1234-5678")
                .address("경기도 성남시 분당구 네이버로23")
                .build();

        owner1 = userRepository.save(owner1);

        // 상점 객체 생성
        StoreCategory category = new StoreCategory(UUID.randomUUID(), "샐러드");
        category = storeCategoryRepository.save(category);

        store = Store.builder()
                .user(owner1)
                .storeCategory(category)
                .storeName("테스트 상점")
                .storeNumber("12345")
                .address("서울특별시 강남구 테헤란로 123")
                .build();
        store = storeRepository.save(store);

        // 제품 객체 생성
        product1 = Product.builder()
                .id(UUID.randomUUID())
                .store(store)
                .name("연어 샐러드")
                .description("설명")
                .price(new BigDecimal("18000"))
                .displayStatus(ProductDisplayStatusEnum.IN_STOCK)
                .maxOrderLimit(10)
                .stockQuantity(55)
                .isHidden(false)
                .build();
        product1 = productRepository.save(product1);

        proHis1 = ProductHistory.builder()
                .id(UUID.randomUUID())
                .productId(product1.getId())
                .name(product1.getName())
                .description(product1.getDescription())
                .price(product1.getPrice())
                .build();
        proHis1 = productHistoryRepository.save(proHis1);

        product2 = Product.builder()
                .id(UUID.randomUUID())
                .store(store)
                .name("케이준치킨 샐러드")
                .description("설명")
                .price(new BigDecimal("15000"))
                .displayStatus(ProductDisplayStatusEnum.PENDING_SALE)
                .maxOrderLimit(100)
                .stockQuantity(55)
                .isHidden(false)
                .build();
        product2 = productRepository.save(product2);

        proHis2 = ProductHistory.builder()
                .id(UUID.randomUUID())
                .productId(product2.getId())
                .name(product2.getName())
                .description(product2.getDescription())
                .price(product2.getPrice())
                .build();
        proHis2 = productHistoryRepository.save(proHis2);

    }

    @Test
    @DisplayName("createOrder 테스트 제대로 수행")
    void createOrderTest() {
        // Arrange: OrderRequestDto 객체 생성
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setStoreId(store.getId());
        requestDto.setOrderType("MEET");
        requestDto.setOrderAddress("서울시 송파구 석촌동");
        requestDto.setOrderStatus("PENDING_PAYMENT");

        List<OrderProductRequestDto> productRequestList = new ArrayList<>();
        OrderProductRequestDto productRequest1 = new OrderProductRequestDto();
        productRequest1.setProductId(product1.getId());
        productRequest1.setOrderQuantity(2);

        productRequestList.add(productRequest1);
        requestDto.setOrderProductList(productRequestList);

        // Act: createOrder 메서드 호출
        UUID createdOrderId = orderService.createOrder(requestDto, customer.getId());

        // Assert: 생성된 Order ID가 null이 아니어야 한다
        assertNotNull(createdOrderId);

        // Order가 repository에 저장되었는지 검증
        Order savedOrder = orderRepository.findById(createdOrderId).orElseThrow();
        assertNotNull(savedOrder);
    }

    @Test
    @DisplayName("createOrder IN_STOCK 상태가 아닐 때")
    void createOrderTestNotInStock() {
        // Arrange: OrderRequestDto 객체 생성
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setStoreId(store.getId());
        requestDto.setOrderType("MEET");
        requestDto.setOrderAddress("서울시 송파구 석촌동");
        requestDto.setOrderStatus("PENDING_PAYMENT");

        List<OrderProductRequestDto> productRequestList = new ArrayList<>();
        OrderProductRequestDto productRequest1 = new OrderProductRequestDto();
        productRequest1.setProductId(product2.getId());
        productRequest1.setOrderQuantity(2);

        productRequestList.add(productRequest1);
        requestDto.setOrderProductList(productRequestList);

        // Act: createOrder 메서드 호출

        assertThrows(InvalidDisplayStatusException.class, () -> orderService.createOrder(requestDto, customer.getId()));

    }

    @Test
    @DisplayName("createOrder 상품의 최대 주문 수량 이상으로 주문했을 때")
    void createOrderTestOverMaxOrderLimit() {
        // Arrange: OrderRequestDto 객체 생성
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setStoreId(store.getId());
        requestDto.setOrderType("MEET");
        requestDto.setOrderAddress("서울시 송파구 석촌동");
        requestDto.setOrderStatus("PENDING_PAYMENT");

        List<OrderProductRequestDto> productRequestList = new ArrayList<>();
        OrderProductRequestDto productRequest1 = new OrderProductRequestDto();
        productRequest1.setProductId(product1.getId());
        productRequest1.setOrderQuantity(11);

        productRequestList.add(productRequest1);
        requestDto.setOrderProductList(productRequestList);

        // Act: createOrder 메서드 호출

        assertThrows(OrderQuantityExceededException.class, () -> orderService.createOrder(requestDto, customer.getId()));

    }

    @Test
    @DisplayName("createOrder 상품의 재고가 모자랄 때")
    void createOrderTestOutOfStock() {
        // Arrange: OrderRequestDto 객체 생성
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setStoreId(store.getId());
        requestDto.setOrderType("MEET");
        requestDto.setOrderAddress("서울시 송파구 석촌동");
        requestDto.setOrderStatus("PENDING_PAYMENT");

        List<OrderProductRequestDto> productRequestList = new ArrayList<>();
        OrderProductRequestDto productRequest1 = new OrderProductRequestDto();
        productRequest1.setProductId(product1.getId());
        productRequest1.setOrderQuantity(60);

        productRequestList.add(productRequest1);
        requestDto.setOrderProductList(productRequestList);

        // Act: createOrder 메서드 호출

        assertThrows(OrderQuantityExceededException.class, () -> orderService.createOrder(requestDto, customer.getId()));

    }
}
