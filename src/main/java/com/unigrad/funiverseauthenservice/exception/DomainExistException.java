package com.unigrad.funiverseauthenservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DomainExistException extends RuntimeException{

    public DomainExistException(String message) {
        super(message);
    }
}