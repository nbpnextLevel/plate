package com.sparta.plate.service.order;

import com.sparta.plate.dto.request.OrderProductRequestDto;
import com.sparta.plate.dto.request.OrderRequestDto;
import com.sparta.plate.dto.response.OrderProductResponseDto;
import com.sparta.plate.dto.response.OrderResponseDto;
import com.sparta.plate.entity.*;
import com.sparta.plate.exception.*;
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
import java.util.*;

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
    private final ProductHistoryRepository productHistoryRepository;

    @Transactional
    public UUID createOrder(OrderRequestDto requestDto, Long userId) {
        OrderTypeEnum.fromString(requestDto.getOrderType());
        OrderStatusEnum.fromString(requestDto.getOrderStatus());

        UUID orderId = UUID.randomUUID();
        while (orderRepository.existsById(orderId)) {
            orderId = UUID.randomUUID();
        }

        // User 객체 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + requestDto.getUserId()));

        // Store 객체 조회
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + requestDto.getStoreId()));

        // DTO에서 Entity로 변환
        Order order = requestDto.toEntity(user, store);
        order.setOrderId(orderId);

        // OrderProduct 중복 합산 처리
        Map<UUID, OrderProduct> productMap = new HashMap<>();
        for (OrderProductRequestDto dto : requestDto.getOrderProductList()) {
            OrderProduct orderProduct = createOrderProduct(dto, order);
            UUID productId = orderProduct.getProduct().getId();

            if (productMap.containsKey(productId)) {
                // 중복된 상품이 있으면 수량을 합산
                OrderProduct existingProduct = productMap.get(productId);
                existingProduct.setOrderQuantity(existingProduct.getOrderQuantity() + orderProduct.getOrderQuantity());
            } else {
                // 새로운 상품이면 Map에 추가
                productMap.put(productId, orderProduct);
            }
        }

        // Map의 값들을 List로 변환하여 Order에 설정
        order.setOrderProductList(new ArrayList<>(productMap.values()));

        // 총 가격 계산
        order.calculateTotalPrice();
        orderRepository.save(order);

        return orderId;
    }

    @Transactional
    public void updateOrder(UUID orderId, UUID storeId, OrderRequestDto requestDto, User user) {

        OrderTypeEnum.fromString(requestDto.getOrderType());
        Order order;

        switch(user.getRole()){
            case CUSTOMER -> order = orderRepository.findByUserIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(user.getId(), orderId, PENDING_PAYMENT)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with ID : " + orderId));

            case OWNER -> {

                if (!user.getStore().getId().equals(storeId)) {
                    throw new UnAuthorizedException("User does not have access to this store.");
                }

                // storeId가 일치하는 경우에만 주문 조회
                order = orderRepository.findByStoreIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(storeId, orderId, PENDING_PAYMENT)
                        .orElseThrow(() -> new OrderNotFoundException("Order not found with ID : " + orderId));

            }

            case MASTER, MANAGER -> order = orderRepository.findByOrderIdAndIsDeletedFalseAndIsCanceledFalseAndOrderStatus(orderId, PENDING_PAYMENT)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with ID : " + orderId));

            default -> throw new UserNotAuthorizedException("User role not recognized");
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
                            throw new DuplicateOrderProductException(dto.getProductId());
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
                            orderProductRepository.delete(productToDelete);

                        } else {
                            throw new OrderProductNotFoundException("OrderProduct not found with ID : " + dto.getOrderProductId());
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
                            throw new OrderProductNotFoundException("OrderProduct not found with ID : " + dto.getOrderProductId());
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("Invalid flag status : " + FlagStatusEnum.valueOf(dto.getFlagStatus()));
                }
            }
        }
        order.calculateTotalPrice();
        orderRepository.saveAndFlush(order);
    }

    @Transactional
    public OrderResponseDto getOrder(UUID orderId, UUID storeId, User user) {

        Order order;

        switch(user.getRole()){
            case CUSTOMER -> order = orderRepository.findByUserIdAndOrderIdAndIsDeletedFalse(user.getId(), orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with ID : " + orderId));

            case OWNER -> {

                if (!user.getStore().getId().equals(storeId)) {
                    throw new UnAuthorizedException("User does not have access to this store.");
                }

                order = orderRepository.findByStoreIdAndOrderIdAndIsDeletedFalse(storeId, orderId)
                        .orElseThrow(() -> new OrderNotFoundException("Order not found with ID : " + orderId));
            }

            case MASTER, MANAGER -> order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with ID : " + orderId));

            default -> throw new UserNotAuthorizedException("User role not recognized");
        }

        // 각 OrderProduct의 productHistoryId를 통해 ProductHistory 정보를 조회하여 DTO 생성
        List<OrderProductResponseDto> orderProductResponseDtoList = order.getOrderProductList().stream()
                .map(orderProduct -> {
                    ProductHistory productHistory = productHistoryRepository.findById(orderProduct.getProductHistoryId())
                            .orElseThrow(() -> new ProductHistoryNotFoundException("Product history not found"));
                    return new OrderProductResponseDto(orderProduct, productHistory);
                })
                .toList();


        return OrderResponseDto.fromEntity(order, orderProductResponseDtoList);
    }

    @Transactional
    public Page<OrderResponseDto> getOrderList(UUID storeId, String orderStatus, LocalDate orderDateFrom, LocalDate orderDateTo, Pageable pageable, User user) {

        Page<Order> orderList;

        switch(user.getRole()) {
            case CUSTOMER ->
                    orderList = orderRepository.findByUserOrdersWithProductList(user.getId(), OrderStatusEnum.valueOf(orderStatus), orderDateFrom, orderDateTo, pageable);

            case OWNER -> {

                if (!user.getStore().getId().equals(storeId)) {
                    throw new UnAuthorizedException("User does not have access to this store.");
                }
                orderList = orderRepository.findByStoreOrdersWithProductList(storeId, OrderStatusEnum.valueOf(orderStatus), orderDateFrom, orderDateTo, pageable);
            }
            case MASTER, MANAGER-> orderList = orderRepository.findByOrdersWithProductList(OrderStatusEnum.valueOf(orderStatus), orderDateFrom, orderDateTo, pageable);

            default -> throw new UserNotAuthorizedException("User role not recognized");
        }

        if (orderList.isEmpty()) {
            throw new OrderNotFoundException("Order not found");
        }
        List<OrderResponseDto> orderResponseDtoList = orderList.stream()
                .map(order -> {
                    List<OrderProductResponseDto> orderProductResponseDtoList = order.getOrderProductList().stream()
                            .map(orderProduct -> {
                                ProductHistory productHistory = productHistoryRepository.findById(orderProduct.getProductHistoryId())
                                        .orElseThrow(() -> new ProductHistoryNotFoundException("Product history not found"));
                                return new OrderProductResponseDto(orderProduct, productHistory);
                            })
                            .toList();

                    return OrderResponseDto.fromEntity(order, orderProductResponseDtoList);
                })
                .toList();

        return new PageImpl<>(orderResponseDtoList, pageable, orderList.getTotalElements());
    }

    @Transactional
    public void deleteOrder(UUID orderId, Long userId) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found or already deleted"));

        // 주문 상태가 취소 또는 완료일 때만 삭제 가능하도록 조건 추가
        if (order.getOrderStatus() != OrderStatusEnum.ORDER_CANCELLED && order.getOrderStatus() != OrderStatusEnum.DELIVERY_COMPLETED) {
            throw new OrderNotFoundException("Order not found with status 'ORDER_CANCELLED' or 'DELIVER_COMPLETED'");
        }

        order.setIsDeleted(true);
        order.markAsDeleted(userId);
        orderRepository.save(order);
    }



    @Transactional
    public void cancelOrder(UUID orderId, UUID storeId, User user) {

        Order order;

        switch(user.getRole()){
            case CUSTOMER -> order = orderRepository.findByUserIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalse(user.getId(), orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found"));

            case OWNER -> {

                if (!user.getStore().getId().equals(storeId)) {
                    throw new UnAuthorizedException("User does not have access to this store.");
                }
                order = orderRepository.findByStoreIdAndOrderIdAndIsDeletedFalseAndIsCanceledFalse(storeId, orderId)
                        .orElseThrow(() -> new OrderNotFoundException("Order not found"));
            }

            case MASTER, MANAGER -> order = orderRepository.findByOrderIdAndIsDeletedFalseAndIsCanceledFalse(orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found"));

            default -> throw new UnAuthorizedException("User role not recognized");
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
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + requestDto.getProductId()));

        if(!product.getStore().getId().equals(order.getStore().getId())){
            throw new ProductHistoryNotFoundException("Product is not in order store");
        }

        OrderProduct orderProduct = requestDto.toEntity(product, order);
        checkQuantity(orderProduct);

        //System.out.println("여기" + productHistoryRepository.findLatestByProductId(requestDto.getProductId()).getId());
        orderProduct.setProductHistoryId(productHistoryRepository.findLatestByProductId(requestDto.getProductId()).getId());
        orderProduct.setOrderProductId(orderProductId); // 고유한 ID 설정


        return orderProduct;
    }

    public void checkQuantity(OrderProduct orderProduct) {
        if(orderProduct.getOrderQuantityLimit()) {
            throw new OrderQuantityExceededException("Your order quantity has exceeded your stock. Stock  : " + orderProduct.getProduct().getStockQuantity());
        }

        if(orderProduct.getOrderLimit()) {
            throw new OrderQuantityExceededException("Your order quantity has exceeded your maximum order limit. Max order limt : " + orderProduct.getProduct().getMaxOrderLimit());
        }

        orderProduct.setProductStockQuantity();
    }
}