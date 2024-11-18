package com.sparta.plate.exception;

public class OrderProductNotFoundException extends IllegalArgumentException {
    public OrderProductNotFoundException(String message) {
        super(message);
    }
}