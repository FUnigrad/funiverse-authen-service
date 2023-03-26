package com.unigrad.funiverseauthenservice.advice;

import com.unigrad.funiverseauthenservice.exception.DomainExistException;
import com.unigrad.funiverseauthenservice.exception.ExpiredTokenException;
import com.unigrad.funiverseauthenservice.exception.InvalidRefreshTokenException;
import com.unigrad.funiverseauthenservice.exception.ServiceCommunicateException;
import com.unigrad.funiverseauthenservice.payload.ErrorMessage;
import com.unigrad.funiverseauthenservice.payload.TokenErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(value = InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public TokenErrorMessage handleTokenRefreshException(InvalidRefreshTokenException ex, WebRequest request) {
        return new TokenErrorMessage(
                request.getDescription(false),
                LocalDateTime.now(),
                "InvalidToken",
                ex.getMessage(),
                TokenErrorMessage.TokenType.REFRESH_TOKEN
        );
    }

    @ExceptionHandler(value = ExpiredTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public TokenErrorMessage handleExpiredRefreshTokenException(ExpiredTokenException ex, WebRequest request) {
        return new TokenErrorMessage(
                request.getDescription(false),
                LocalDateTime.now(),
                "ExpiredToken",
                ex.getMessage(),
                ex.getTokenType()
        );
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class, DomainExistException.class, ServiceCommunicateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUsernameNotFoundException(RuntimeException ex, WebRequest request) {

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleRunTimeException(RuntimeException ex, WebRequest request) {

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }
}