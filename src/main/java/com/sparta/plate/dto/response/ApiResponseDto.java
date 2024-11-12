package com.sparta.plate.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {
    private int statusCode;
    private String statusMessage;
    private String message;
    private Map<String, Object> content;

    private T data;

    // 메시지 없는 성공 응답
    public static <T> ApiResponseDto<T> success(T data) {
        return ApiResponseDto.<T>builder()
            .statusCode(HttpStatus.OK.value())
            .statusMessage(HttpStatus.OK.getReasonPhrase())
            .data(data)
            .build();
    }

    // 메시지를 포함한 성공 응답
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return ApiResponseDto.<T>builder()
            .statusCode(HttpStatus.OK.value())
            .statusMessage(HttpStatus.OK.getReasonPhrase())
            .message(message)
            .data(data)
            .build();
    }
}
