package com.trabean.exception;

public class UnauthorizedUserRoleException extends RuntimeException {

    private static final UnauthorizedUserRoleException INSTANCE = new UnauthorizedUserRoleException("Unauthorized user role");

    private UnauthorizedUserRoleException(String message) {
        super(message);
    }

    public static UnauthorizedUserRoleException getInstance() {
        return INSTANCE;
    }
}
