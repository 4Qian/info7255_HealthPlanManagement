package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 404
 */
public class PreconditionFailedException extends ResponseStatusException {
    public PreconditionFailedException(String reason) {
        super(HttpStatus.PRECONDITION_FAILED, reason);
    }
}
