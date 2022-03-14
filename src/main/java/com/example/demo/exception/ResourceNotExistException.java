package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 404
 */
public class ResourceNotExistException extends ResponseStatusException {
    public ResourceNotExistException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
}
