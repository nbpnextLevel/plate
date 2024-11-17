package com.sparta.plate.exception;

import java.util.UUID;

public class DuplicateOrderProductException extends RuntimeException {
    public DuplicateOrderProductException(UUID productId) {
        super("Product with ID " + productId + " is already in the order.");
    }
}