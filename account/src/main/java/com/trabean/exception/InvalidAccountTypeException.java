package com.trabean.exception;

public class InvalidAccountTypeException extends RuntimeException {
  public InvalidAccountTypeException(String message) {
    super(message);
  }
}
