package com.sparta.plate.exception;

public class OrderProductNotFoundException extends RuntimeException {
    public OrderProductNotFoundException(String message) {
        super(message);
    }
}