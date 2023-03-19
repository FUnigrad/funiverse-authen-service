package com.unigrad.funiverseauthenservice.exception;

import com.unigrad.funiverseauthenservice.payload.TokenErrorMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
@Getter
public class ExpiredTokenException extends RuntimeException{

    private final TokenErrorMessage.TokenType tokenType;

    public ExpiredTokenException(String message, TokenErrorMessage.TokenType tokenType) {
        super(message);
        this.tokenType = tokenType;
    }
}