package com.sparta.plate.exception;

public class ProductHistoryNotFoundException extends RuntimeException {
    public ProductHistoryNotFoundException(String message) {
        super(message);
    }
}