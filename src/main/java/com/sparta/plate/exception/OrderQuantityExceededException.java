package com.sparta.plate.exception;

public class OrderQuantityExceededException extends RuntimeException  {
    public OrderQuantityExceededException(String message) {
        super(message);
    }
}