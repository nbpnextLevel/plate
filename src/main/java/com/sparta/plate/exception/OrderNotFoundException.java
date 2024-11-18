package com.sparta.plate.exception;

public class OrderNotFoundException extends IllegalArgumentException {
    public OrderNotFoundException(String s) {
        super(s);
    }
}
