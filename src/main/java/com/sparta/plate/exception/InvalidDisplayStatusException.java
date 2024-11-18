package com.sparta.plate.exception;

public class InvalidDisplayStatusException extends IllegalArgumentException {
    public InvalidDisplayStatusException(String message) {
        super(message);
    }
}