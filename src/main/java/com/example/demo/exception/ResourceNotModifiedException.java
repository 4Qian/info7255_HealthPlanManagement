package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 404
 */
public class ResourceNotModifiedException extends ResponseStatusException {
    public ResourceNotModifiedException(String reason) {
        super(HttpStatus.NOT_MODIFIED, reason);
    }
}
