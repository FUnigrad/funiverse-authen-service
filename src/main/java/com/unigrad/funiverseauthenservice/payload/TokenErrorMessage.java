package com.unigrad.funiverseauthenservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenErrorMessage {

    private String description;
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private TokenType tokenType;

    public enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN,
    }
}