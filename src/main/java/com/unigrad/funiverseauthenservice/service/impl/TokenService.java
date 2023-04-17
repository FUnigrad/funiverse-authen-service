package com.unigrad.funiverseauthenservice.service.impl;

import com.unigrad.funiverseauthenservice.entity.Token;
import com.unigrad.funiverseauthenservice.repository.ITokenRepository;
import com.unigrad.funiverseauthenservice.service.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {

    private final ITokenRepository tokenRepository;

    @Override
    public Optional<Token> findByTokenAndType(String token, Token.Type type) {
        return tokenRepository.findByTokenAndType(token, type);
    }

    @Override
    public void deleteTokensByUser_PersonalMailAndType(String username, Token.Type type) {
        tokenRepository.deleteTokensByUser_PersonalMailAndType(username, type);
    }

    @Override
    public Token save(Token token) {
        return tokenRepository.save(token);
    }

    @Override
    public void delete(Long id) {
        tokenRepository.deleteById(id);
    }
}