package com.sparta.plate.exception;

public class StoreCategoryNotFoundException extends RuntimeException {
    public StoreCategoryNotFoundException(String message) {
        super(message);
    }
}