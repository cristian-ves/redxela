package com.redxela.api.error;

import com.redxela.domain.exception.CapacityExceededException;
import com.redxela.domain.exception.EntityNotFoundException;
import com.redxela.domain.exception.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * The only place where domain exceptions become HTTP responses, so the
 * controllers just call the domain and let errors travel up. There is no
 * catch-all handler on purpose: it would also swallow the framework's own
 * errors (unknown path, wrong method) and report them as server failures.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(EntityNotFoundException failure) {
        return new ApiError(404, failure.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError invalid(InvalidRequestException failure) {
        return new ApiError(400, failure.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError unreadable(HttpMessageNotReadableException failure) {
        return new ApiError(400, "The request body is missing, is not valid JSON or lacks a required field");
    }

    @ExceptionHandler(CapacityExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError full(CapacityExceededException failure) {
        return new ApiError(409, failure.getMessage());
    }
}