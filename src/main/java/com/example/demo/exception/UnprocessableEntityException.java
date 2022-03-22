package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 404
 */
public class UnprocessableEntityException extends ResponseStatusException {
    public UnprocessableEntityException(String reason) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, reason);
    }
}
