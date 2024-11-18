package com.sparta.plate.exception;

public class StatusChangeException extends IllegalArgumentException {
    public StatusChangeException(String message) {
        super(message);
    }
}