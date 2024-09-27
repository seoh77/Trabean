package com.trabean.travel.exception;

public class InternalServerStatusException extends RuntimeException {
    public InternalServerStatusException(String message) {
        super(message);
    }
}
