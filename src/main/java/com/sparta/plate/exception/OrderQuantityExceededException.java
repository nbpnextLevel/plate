package com.sparta.plate.exception;


public class OrderQuantityExceededException extends IllegalArgumentException  {

    public OrderQuantityExceededException(String message) {
        super(message);
    }
}