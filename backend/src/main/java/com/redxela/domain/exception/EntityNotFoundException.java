package com.redxela.domain.exception;

/* A client, product, order or other entity referenced by a request does not exist. */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
