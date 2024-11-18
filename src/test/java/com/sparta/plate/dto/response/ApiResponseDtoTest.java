package com.sparta.plate.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiResponseDtoTest {

    @Test
    void createApiResponseDto() {
        ApiResponseDto<Object> responseDto = ApiResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message("성공")
                .build();

        assertNotNull(responseDto);
        assertEquals("성공", responseDto.getMessage());
    }
}