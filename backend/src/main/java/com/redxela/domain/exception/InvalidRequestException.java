package com.redxela.domain.exception;

/* A request is well formed but breaks a business rule (empty order, mixed merchandise types, bad quantity). */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
