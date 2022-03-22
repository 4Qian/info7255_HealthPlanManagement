package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 404
 */
public class UnauthorizedException extends ResponseStatusException {
    public UnauthorizedException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}
