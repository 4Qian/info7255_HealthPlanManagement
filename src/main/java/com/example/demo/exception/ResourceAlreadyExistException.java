package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceAlreadyExistException extends ResponseStatusException {
    public ResourceAlreadyExistException(String reason) {
        super(HttpStatus.CONFLICT, reason);
    }
}
