package com.sparta.plate.controller.order;

import com.sparta.plate.dto.request.OrderRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.OrderResponseDto;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
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

    @GetMapping
    public ApiResponseDto<Map<String, Object>> getOrderList(@RequestParam(required = false) String orderStatus,
                                                               @RequestParam(required = false) String orderDateFrom,
                                                               @RequestParam(required = false) String orderDateTo,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "20") int size,
                                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(defaultValue = "desc") String sortOrder,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 날짜 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(orderDateFrom, formatter);
        LocalDate endDate = LocalDate.parse(orderDateTo, formatter);

        // Sort 설정
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        // PageRequest 생성
        Pageable pageable = PageRequest.of(page - 1, size, sort); // page는 0-based index

        Page<OrderResponseDto> orderList = orderService.getOrderList(orderStatus, startDate, endDate, pageable, userDetails.getUser());

        // 응답으로 Page<OrderResponseDto>를 리스트로 변환하여 전달
        Map<String, Object> response = new HashMap<>();
        response.put("orderList", orderList.getContent()); // 실제 주문 목록
        response.put("currentPage", orderList.getNumber() + 1); // 현재 페이지
        response.put("totalItems", orderList.getTotalElements()); // 총 아이템 수
        response.put("totalPages", orderList.getTotalPages()); // 총 페이지 수
        response.put("pageSize", orderList.getSize()); // 페이지 크기

        return ApiResponseDto.success(response);
    }


    @PatchMapping("/{orderId}")
    public ApiResponseDto<Map<String, Object>> updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        orderService.updateOrder(orderId, requestDto, userDetails.getUser());
        return ApiResponseDto.success("수정 완료되었습니다.", Map.of("orderId", orderId));
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
