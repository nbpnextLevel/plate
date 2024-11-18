package com.sparta.plate.google.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GoogleApiService {

    private final RestTemplate restTemplate;

    public GoogleApiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Value("${google.api.key}")
    private String apiKey;

    public Map<String, Object> generateContent(String text) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> partsMap = new HashMap<>();
        Map<String, Object> contentMap = new HashMap<>();

        partsMap.put("text", text);
        contentMap.put("parts", Collections.singletonList(partsMap));
        requestBodyMap.put("contents", Collections.singletonList(contentMap));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";
        try {
            requestBody = objectMapper.writeValueAsString(requestBodyMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        log.info("Google API Response Body: " + responseEntity.getBody());
        log.info("Google API Status Code : " + responseEntity.getStatusCode());

        Map<String, Object> result = new HashMap<>();
        result.put("responseText", fromJSONtoText(responseEntity.getBody()));
        result.put("statusCode", responseEntity.getStatusCode().toString());
        result.put("timestamp", LocalDateTime.now().toString());

        return result;
    }


    private String fromJSONtoText(String jsonResponse) {
        try {
            // ObjectMapper를 이용해 JSON을 Map으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);

            // "candidates" 배열에서 첫 번째 요소를 가져옴
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0); // 첫 번째 후보를 선택
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");

                // "content" 안의 "parts" 배열에서 첫 번째 요소를 가져옴
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    String text = (String) parts.get(0).get("text");
                    if (text != null && !text.isEmpty()) {
                        return text; // 정상적으로 텍스트 반환
                    }
                }
            }

            // "responseText"가 없거나 구조가 다를 때
            return "No response text found in the API response.";
        } catch (Exception e) {
            // 예외 발생 시, 에러 메시지 반환
            e.printStackTrace();
            return "Error processing response";
        }
    }

}