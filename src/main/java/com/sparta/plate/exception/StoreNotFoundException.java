package com.sparta.plate.exception;

public class StoreNotFoundException extends IllegalArgumentException {
    public StoreNotFoundException(String message) {
        super(message);
    }
}