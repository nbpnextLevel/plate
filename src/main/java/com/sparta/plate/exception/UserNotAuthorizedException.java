package com.sparta.plate.exception;

public class UserNotAuthorizedException extends RuntimeException {
    public UserNotAuthorizedException(String s) {
        super(s);
    }
}
