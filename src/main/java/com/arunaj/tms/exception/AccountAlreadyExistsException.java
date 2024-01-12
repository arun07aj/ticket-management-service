package com.arunaj.tms.exception;

public class AccountAlreadyExistsException extends RuntimeException{
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
