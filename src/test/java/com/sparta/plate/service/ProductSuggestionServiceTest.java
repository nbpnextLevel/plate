package com.sparta.plate.service;

import com.sparta.plate.entity.ProductSuggestionRequest;
import com.sparta.plate.google.service.GoogleApiService;
import com.sparta.plate.repository.ProductSuggestionRepository;
import com.sparta.plate.service.product.ProductSuggestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

// 상품 정보 추천 제안 도메인(AI) 테스트 코드
@ExtendWith(MockitoExtension.class)
class ProductSuggestionServiceTest {
    @Mock
    private GoogleApiService googleApiService;

    @Mock
    private ProductSuggestionRepository suggestionRepository;

    @InjectMocks
    private ProductSuggestionService productSuggestionService;

    @Test
    @DisplayName("상품 정보 제안 요청 테스트 (성공) - Google API 정상 응답")
    void getProductSuggestionSuccess() {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("responseText", "Suggested Product Info");
        responseData.put("statusCode", "200 OK");
        responseData.put("timestamp", "2024-11-17T10:00:00");

        when(googleApiService.generateContent(any())).thenReturn(responseData);

        ProductSuggestionRequest savedSuggestionRequest = ProductSuggestionRequest.builder()
                .requestText("Sample Request Text")
                .responseText("Suggested Product Info")
                .requestAt(LocalDateTime.now())
                .responseAt(LocalDateTime.now())
                .isSuccess(true)
                .responseStatus("200 OK")
                .build();

        when(suggestionRepository.save(any(ProductSuggestionRequest.class))).thenReturn(savedSuggestionRequest);

        String response = productSuggestionService.getProductSuggestion("Sample Request Text", LocalDateTime.now());

        assertEquals("Suggested Product Info", response);
        verify(suggestionRepository, times(1)).save(any(ProductSuggestionRequest.class));
    }

    @Test
    @DisplayName("상품 정보 제안 요청 테스트 (실패) - Google API 응답 실패")
    void getProductSuggestionFail() {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("responseText", "Failed");
        responseData.put("statusCode", "500 Internal Server Error");
        responseData.put("timestamp", "2024-11-17T10:00:00");

        when(googleApiService.generateContent(any())).thenReturn(responseData);

        ProductSuggestionRequest savedSuggestionRequest = ProductSuggestionRequest.builder()
                .requestText("Sample Request Text")
                .responseText("Failed")
                .requestAt(LocalDateTime.now())
                .responseAt(LocalDateTime.now())
                .isSuccess(false)
                .responseStatus("500 Internal Server Error")
                .build();

        when(suggestionRepository.save(any(ProductSuggestionRequest.class))).thenReturn(savedSuggestionRequest);

        String response = productSuggestionService.getProductSuggestion("Sample Request Text", LocalDateTime.now());

        assertEquals("Failed", response);
        verify(suggestionRepository, times(1)).save(any(ProductSuggestionRequest.class));  // save 메서드가 한 번 호출됐는지 확인
    }

}
