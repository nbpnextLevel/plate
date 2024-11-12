package com.sparta.plate.exception;

public class UnAuthorizedException extends IllegalArgumentException {
	public UnAuthorizedException(String message) {
		super(message);
	}
}
