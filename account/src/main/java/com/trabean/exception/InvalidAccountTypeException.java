package com.trabean.exception;

public class InvalidAccountTypeException extends RuntimeException {

    private static final InvalidAccountTypeException INSTANCE = new InvalidAccountTypeException("Invalid account type");

    private InvalidAccountTypeException(String message) {
        super(message);
    }

    public static InvalidAccountTypeException getInstance() {
        return INSTANCE;
    }
}
