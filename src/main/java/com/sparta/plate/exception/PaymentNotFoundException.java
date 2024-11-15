package com.sparta.plate.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String s) {
        super(s);
    }
}
