package com.sparta.plate.controller;

import com.sparta.plate.dto.request.ProductRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/")
    public ApiResponseDto createProduct(@RequestBody ProductRequestDto requestDto) {
        UUID savedProductId = productService.createProduct(requestDto);

        return ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(Map.of("id", savedProductId))
                .build();
    }
}