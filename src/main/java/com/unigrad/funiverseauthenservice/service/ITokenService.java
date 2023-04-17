package com.unigrad.funiverseauthenservice.service;

import com.unigrad.funiverseauthenservice.entity.Token;

import java.util.Optional;

public interface ITokenService {

    Optional<Token> findByTokenAndType(String token, Token.Type type);

    void deleteTokensByUser_PersonalMailAndType(String username, Token.Type type);

    Token save(Token token);

    void delete(Long id);
}