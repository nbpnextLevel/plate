package com.sparta.plate.exception;

public class ProductIsDeletedException extends RuntimeException {
    public ProductIsDeletedException(String message) {
        super(message);
    }
}