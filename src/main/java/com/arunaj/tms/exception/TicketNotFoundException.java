package com.arunaj.tms.exception;

public class TicketNotFoundException extends RuntimeException{
    public TicketNotFoundException(String message) {
        super(message);
    }
}
