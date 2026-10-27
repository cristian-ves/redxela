package com.redxela.api.error;

/* The body of every error response, so the interface can show "message" without guessing the shape. */
public record ApiError(int status, String message) {
}