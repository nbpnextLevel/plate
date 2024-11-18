package com.sparta.plate.exception;

public class NotFoundPaymentException extends RuntimeException {
    public NotFoundPaymentException(String s) {
        super(s);
    }
}
