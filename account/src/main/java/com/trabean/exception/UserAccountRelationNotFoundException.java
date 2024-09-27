package com.trabean.exception;

public class UserAccountRelationNotFoundException extends RuntimeException {

    private static final UserAccountRelationNotFoundException INSTANCE = new UserAccountRelationNotFoundException("User account relation not found");

    private UserAccountRelationNotFoundException(String message) {
        super(message);
    }

    public static UserAccountRelationNotFoundException getInstance() {
        return INSTANCE;
    }
}
