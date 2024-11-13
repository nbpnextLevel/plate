package com.sparta.plate.exception;

public class ProductOwnerMismatchException extends RuntimeException {
    public ProductOwnerMismatchException(String message) {
        super(message);
    }
}