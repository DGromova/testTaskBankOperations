package com.bank.exception;

public class AuthUsersAccountNotFoundException extends RuntimeException {

    public AuthUsersAccountNotFoundException(String message) {
        super(message);
    }

}
