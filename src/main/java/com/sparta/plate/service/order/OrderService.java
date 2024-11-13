package com.sparta.plate.service.order;

import com.sparta.plate.dto.request.OrderRequestDto;
import com.sparta.plate.dto.response.OrderResponseDto;
import com.sparta.plate.entity.*;
import com.sparta.plate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public UUID createOrder(OrderRequestDto requestDto, Long userId) {
        UUID orderId = UUID.randomUUID();
        while (orderRepository.existsByOrderId(orderId)) {
            orderId = UUID.randomUUID();
        }

        // User 객체 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + requestDto.getUserId()));

        // Store 객체 조회
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found with ID: " + requestDto.getStoreId()));

        // DTO에서 Entity로 변환
        Order order = requestDto.toEntity(user, store);
        order.setOrderId(orderId);

        order.setOrderProductList(requestDto.getOrderProductList().stream()
                .map(dto -> {
                    UUID orderProductId = UUID.randomUUID();
                    while (orderProductRepository.existsByOrderProductId(orderProductId)) {
                        orderProductId = UUID.randomUUID();
                    }

                    // Product 객체 조회 로직을 추가 (예: productRepository.findById() 등)
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found with name: " + dto.getProductId()));

                    OrderProduct orderProduct = dto.toEntity(product, order);

                    if(orderProduct.getOrderQuantityLimit()) {
                        throw new IllegalArgumentException("Your order quantity has exceeded your stock. Stock  : " + orderProduct.getProduct().getStockQuantity());
                    }

                    if(orderProduct.getOrderLimit()) {
                        throw new IllegalArgumentException("Your order quantity has exceeded your maximum order limit. Max order limt : " + orderProduct.getProduct().getMaxOrderLimit());
                    }

                    orderProduct.setProductStockQuantity();
                    orderProduct.setOrderProductId(orderProductId); // 고유한 ID 설정
                    return orderProduct;
                }).toList());

        // 총 가격 계산
        order.calculateTotalPrice();
        orderRepository.save(order);

        return orderId;
    }

    @Transactional
    public OrderResponseDto getOrder(UUID orderId, User user) {

        Order order;

        switch(user.getRole()){
            case CUSTOMER -> order = orderRepository.findByUserIdAndOrderIdAndIsDeletedFalse(user.getId(), orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            case OWNER -> order = orderRepository.findByStoreIdAndOrderIdAndIsDeletedFalse(user.getStore().getId(), orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            default -> order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        }

        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public void deleteOrder(UUID orderId, Long userId) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found or already deleted"));
        order.setIsDeleted(true);
        order.markAsDeleted(userId);
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(UUID orderId, User user) {

        Order order;

        switch(user.getRole()){
            case CUSTOMER -> order = orderRepository.findByUserIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalse(user.getId(), orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            case OWNER -> order = orderRepository.findByStoreIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalse(user.getStore().getId(), orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            default -> order = orderRepository.findByOrderIdAndIsDeletedFalseAndIsCanceledFalse(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        }

        if (Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes() > 5) {
            throw new IllegalStateException("Order cannot be canceled after 5 minutes.");
        }
        order.cancelOrder();
        orderRepository.save(order);
    }
}
