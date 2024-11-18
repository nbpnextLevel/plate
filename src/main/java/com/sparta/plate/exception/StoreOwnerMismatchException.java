package com.sparta.plate.exception;

public class StoreOwnerMismatchException extends RuntimeException {
    public StoreOwnerMismatchException(String message) {
        super(message);
    }
}