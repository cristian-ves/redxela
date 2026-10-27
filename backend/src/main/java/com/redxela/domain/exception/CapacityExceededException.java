package com.redxela.domain.exception;

/* A bounded structure (arrival queue, warehouse, ...) cannot take more work right now. */
public class CapacityExceededException extends RuntimeException {

    public CapacityExceededException(String message) {
        super(message);
    }
}