package com.sparta.plate.controller.order;

import com.sparta.plate.dto.request.OrderRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.OrderResponseDto;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j(topic = "Order Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponseDto<Map<String, Object>> createOrder(@RequestBody OrderRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UUID orderId = orderService.createOrder(requestDto, userDetails.getUser().getId());

        return ApiResponseDto.success("주문 완료되었습니다.", Map.of("orderId", orderId));
    }

    @GetMapping("/{orderId}")
    public ApiResponseDto<OrderResponseDto> getOrder(@PathVariable("orderId") UUID orderId, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponseDto.success(orderService.getOrder(orderId, userDetails.getUser()));
    }

    @PatchMapping("/delete/{orderId}")
    public ApiResponseDto<Map<String, Object>> deleteOrder(@PathVariable UUID orderId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        orderService.deleteOrder(orderId, userDetails.getUser().getId());
        return ApiResponseDto.success("삭제 완료되었습니다.", Map.of("orderId", orderId));
    }

    @PatchMapping("/cancel/{orderId}")
    public ApiResponseDto<Map<String, Object>> cancelOrder(@PathVariable UUID orderId, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        orderService.cancelOrder(orderId, userDetails.getUser());

        return ApiResponseDto.success("취소 완료되었습니다.", Map.of("orderId", orderId));
    }
}
