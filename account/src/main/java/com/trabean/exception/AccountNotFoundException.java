package com.trabean.exception;

public class AccountNotFoundException extends RuntimeException {

    private static final AccountNotFoundException INSTANCE = new AccountNotFoundException("Account not found");

    private AccountNotFoundException(String message) {
        super(message);
    }

    public static AccountNotFoundException getInstance() {
        return INSTANCE;
    }
}
