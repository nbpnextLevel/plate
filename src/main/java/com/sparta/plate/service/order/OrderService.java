package com.sparta.plate.service.order;

import com.sparta.plate.dto.request.OrderProductRequestDto;
import com.sparta.plate.dto.request.OrderRequestDto;
import com.sparta.plate.dto.response.OrderResponseDto;
import com.sparta.plate.entity.*;
import com.sparta.plate.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.sparta.plate.entity.OrderStatusEnum.PENDING_PAYMENT;

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
        while (orderRepository.existsById(orderId)) {
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
                .map(dto -> createOrderProduct(dto, order))
                .toList());

        // 총 가격 계산
        order.calculateTotalPrice();
        orderRepository.save(order);

        return orderId;
    }

    @Transactional
    public void updateOrder(UUID orderId, OrderRequestDto requestDto, User user) {

        Order order;

        switch(user.getRole()){
            case CUSTOMER -> order = orderRepository.findByUserIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(user.getId(), orderId, PENDING_PAYMENT)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            case OWNER -> order = orderRepository.findByStoreIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(user.getStore().getId(), orderId, PENDING_PAYMENT)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            case MASTER, MANAGER -> order = orderRepository.findByOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(orderId, PENDING_PAYMENT)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            default -> throw new IllegalArgumentException("User role not recognized");
        }

        if(requestDto.getOrderType() != null) {
            order.setOrderType(OrderTypeEnum.valueOf(requestDto.getOrderType()));
        }

        if(requestDto.getOrderAddress() != null) {
            order.setOrderAddress(requestDto.getOrderAddress());
        }

        if(requestDto.getOrderRequest() != null) {
            order.setOrderRequest(requestDto.getOrderRequest());
        }

        if(requestDto.getOrderProductList() != null && !requestDto.getOrderProductList().isEmpty()) {
            for (OrderProductRequestDto dto : requestDto.getOrderProductList()) {
                Optional<OrderProduct> existingProductOpt = order.getOrderProductList().stream()
                        .filter(op -> op.getProduct().getId().equals(dto.getProductId()))
                        .findFirst();

                switch (FlagStatusEnum.valueOf(dto.getFlagStatus())) {
                    case CREATE:

                        if (existingProductOpt.isPresent()) {
                            throw new IllegalArgumentException("Product with ID " + dto.getProductId() + " is already in the order.");
                        } else {
                            // Create a new OrderProduct and add to the list
                            OrderProduct newProduct = createOrderProduct(dto, order);
                            order.getOrderProductList().add(newProduct);
                        }
                        break;

                    case DELETE:
                        if (existingProductOpt.isPresent()) {
                            OrderProduct productToDelete = existingProductOpt.get();
                            order.getOrderProductList().remove(productToDelete);
                            productToDelete.resetProductStockQuantity();
                            System.out.println(productToDelete.getOrderProductId());
                            orderProductRepository.delete(productToDelete);

                        } else {
                            throw new IllegalArgumentException("OrderProduct not found with id: " + dto.getOrderProductId());
                        }
                        break;

                    case UPDATE:

                        if (existingProductOpt.isPresent()) {
                            OrderProduct productToUpdate = existingProductOpt.get();
                            if (dto.getOrderQuantity() != productToUpdate.getOrderQuantity()) {
                                productToUpdate.resetProductStockQuantity();
                                productToUpdate.setOrderQuantity(dto.getOrderQuantity());
                                productToUpdate.setProductStockQuantity();
                            }
                        } else {
                            throw new IllegalArgumentException("OrderProduct not found with id: " + dto.getOrderProductId());
                        }
                        break;

                    default:
                         throw new IllegalArgumentException("Invalid flag status");
                }
            }
        }
        order.calculateTotalPrice();
        orderRepository.saveAndFlush(order);
    }

    @Transactional
    public OrderResponseDto getOrder(UUID orderId, User user) {

        Order order;

        switch(user.getRole()){
            case CUSTOMER -> order = orderRepository.findByUserIdAndOrderIdAndIsDeletedFalse(user.getId(), orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            case OWNER -> order = orderRepository.findByStoreIdAndOrderIdAndIsDeletedFalse(user.getStore().getId(), orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            case MASTER, MANAGER -> order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            default -> throw new IllegalArgumentException("User role not recognized");

        }

        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public Page<OrderResponseDto> getOrderList(String orderStatus, LocalDate orderDateFrom, LocalDate orderDateTo, Pageable pageable, User user) {

        Page<Order> orderList;

        switch(user.getRole()){
            case CUSTOMER -> orderList = orderRepository.findByUserOrdersWithProductList(user.getId(), OrderStatusEnum.valueOf(orderStatus), orderDateFrom, orderDateTo, pageable);

            case OWNER -> orderList = orderRepository.findByStoreOrdersWithProductList(user.getStore().getId(), OrderStatusEnum.valueOf(orderStatus), orderDateFrom, orderDateTo, pageable);

            case MASTER, MANAGER-> orderList = orderRepository.findByOrdersWithProductList(OrderStatusEnum.valueOf(orderStatus), orderDateFrom, orderDateTo, pageable);

            default -> throw new IllegalArgumentException("User role not recognized");
        }

        if (orderList.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }
        List<OrderResponseDto> orderResponseDtoList = orderList.stream()
                                                                .map(OrderResponseDto::fromEntity)
                                                                .collect(Collectors.toList());
        return new PageImpl<>(orderResponseDtoList, pageable, orderList.getTotalElements());
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

            case MASTER, MANAGER -> order = orderRepository.findByOrderIdAndIsDeletedFalseAndIsCanceledFalse(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            default -> throw new IllegalArgumentException("User role not recognized");
        }

        if (Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes() > 5) {
            throw new IllegalStateException("Order cannot be canceled after 5 minutes.");
        }

        order.getOrderProductList()
                .forEach(orderProduct -> {
                    orderProduct.resetProductStockQuantity();
                    orderProduct.setOrderQuantity(0);
                });

        order.cancelOrder();
        order.calculateTotalPrice();
        orderRepository.save(order);
    }

    @Transactional
    public OrderProduct createOrderProduct(OrderProductRequestDto requestDto, Order order) {

        UUID orderProductId = UUID.randomUUID();
        while (orderProductRepository.existsById(orderProductId)) {
            orderProductId = UUID.randomUUID();
        }

        // Product 객체 조회 로직을 추가 (예: productRepository.findById() 등)
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + requestDto.getProductId()));

        if(!product.getStore().getId().equals(order.getStore().getId())){
            throw new IllegalArgumentException("Product is not in order store");
        }

        OrderProduct orderProduct = requestDto.toEntity(product, order);
        checkQuantity(orderProduct);

        orderProduct.setOrderProductId(orderProductId); // 고유한 ID 설정
        return orderProduct;
    }

    public void checkQuantity(OrderProduct orderProduct) {
        if(orderProduct.getOrderQuantityLimit()) {
            throw new IllegalArgumentException("Your order quantity has exceeded your stock. Stock  : " + orderProduct.getProduct().getStockQuantity());
        }

        if(orderProduct.getOrderLimit()) {
            throw new IllegalArgumentException("Your order quantity has exceeded your maximum order limit. Max order limt : " + orderProduct.getProduct().getMaxOrderLimit());
        }

        orderProduct.setProductStockQuantity();
    }
}
