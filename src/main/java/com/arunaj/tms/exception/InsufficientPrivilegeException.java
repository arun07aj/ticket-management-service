package com.arunaj.tms.exception;

public class InsufficientPrivilegeException extends RuntimeException {
    public InsufficientPrivilegeException(String message) {
        super(message);
    }
}
