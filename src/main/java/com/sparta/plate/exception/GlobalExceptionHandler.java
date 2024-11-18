package com.sparta.plate.exception;

import com.sparta.plate.dto.response.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler({IllegalArgumentException.class})
    // public ResponseEntity<RestApiException> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
    //     RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    //     return new ResponseEntity<>(
    //             restApiException,
    //             HttpStatus.BAD_REQUEST
    //     );
    // }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<RestApiException> nullPointerExceptionHandler(NullPointerException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({ProductNotFoundException.class})
    public ResponseEntity<RestApiException> notFoundProductExceptionHandler(ProductNotFoundException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({ProductImageNotFoundException.class})
    public ResponseEntity<RestApiException> handleProductImageNotFoundException(ProductImageNotFoundException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({ProductHistoryNotFoundException.class})
    public ResponseEntity<RestApiException> handleProductImageNotFoundException(ProductHistoryNotFoundException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({ProductIsDeletedException.class})
    public ResponseEntity<RestApiException> handleProductIsDeletedException(ProductIsDeletedException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({ProductOwnerMismatchException.class})
    public ResponseEntity<RestApiException> handleProductOwnerMismatchException(ProductOwnerMismatchException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({StoreOwnerMismatchException.class})
    public ResponseEntity<RestApiException> handleProductOwnerMismatchException(StoreOwnerMismatchException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({ProductValidationException.class})
    public ResponseEntity<RestApiException> handleProductValidationException(ProductValidationException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({UnauthorizedAccessException.class})
    public ResponseEntity<RestApiException> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMessages = new StringBuilder();

        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMessages.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }

        return ResponseEntity.badRequest().body(ApiResponseDto.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage("Validation Failed")
                .message(errorMessages.toString())
                .build());
    }


    @ExceptionHandler({OrderNotFoundException.class})
    public ResponseEntity<RestApiException> handleOrderNotFoundException(OrderNotFoundException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({UserNotAuthorizedException.class})
    public ResponseEntity<RestApiException> UserNotAuthorizedException(UserNotAuthorizedException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({PaymentNotFoundException.class})
    public ResponseEntity<RestApiException> PaymentNotFoundException(PaymentNotFoundException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({StoreNotFoundException.class})
    public ResponseEntity<RestApiException> StoreNotFoundException(StoreNotFoundException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.NOT_FOUND
        );
    }


}