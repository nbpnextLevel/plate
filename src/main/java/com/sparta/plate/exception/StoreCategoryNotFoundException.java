package com.sparta.plate.exception;

public class StoreCategoryNotFoundException extends IllegalArgumentException {
    public StoreCategoryNotFoundException(String message) {
        super(message);
    }
}