package com.sparta.plate.exception;

import jakarta.persistence.EntityNotFoundException;

public class ProductIsDeletedException extends EntityNotFoundException {
    public ProductIsDeletedException(String message) {
        super(message);
    }
}