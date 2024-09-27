package com.trabean.exception;

public class InvalidPasswordException extends RuntimeException {

    private static final InvalidPasswordException INSTANCE = new InvalidPasswordException("Invalid password");

    private InvalidPasswordException(String message) {
        super(message);
    }

    public static InvalidPasswordException getInstance() {
        return INSTANCE;
    }
}
