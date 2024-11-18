package com.sparta.plate.exception;

import java.util.UUID;

public class StatusChangeException extends IllegalArgumentException {
    public StatusChangeException(String message) {
        super(message);
    }
}