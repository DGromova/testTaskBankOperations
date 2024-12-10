package com.bank.exception;

public class ArgumentValidationException extends RuntimeException {
    public ArgumentValidationException(String message) {
        super(message);
    }
}
