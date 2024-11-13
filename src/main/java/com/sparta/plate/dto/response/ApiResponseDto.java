package com.sparta.plate.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
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
    private PageInfo pageInfo;
    private T data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;

        public static PageInfo of(Page<?> page) {
            return PageInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
        }

    }

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

    // 삭제 성공시 응답
    public static <T> ApiResponseDto<T> successDelete() {
        return ApiResponseDto.<T>builder()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .statusMessage(HttpStatus.NO_CONTENT.getReasonPhrase())
            .build();
    }

    // 페이징처리가 필요한 조회 성공 응답
    public static <T> ApiResponseDto<List<T>> successPage(Page<T> page) {
        return ApiResponseDto.<List<T>>builder()
            .statusCode(HttpStatus.OK.value())
            .statusMessage(HttpStatus.OK.getReasonPhrase())
            .pageInfo(PageInfo.of(page))
            .data(page.getContent())
            .build();
    }
}
