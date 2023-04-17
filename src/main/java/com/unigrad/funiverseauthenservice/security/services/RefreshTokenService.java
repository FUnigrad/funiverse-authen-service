package com.unigrad.funiverseauthenservice.security.services;

import com.unigrad.funiverseauthenservice.entity.Token;
import com.unigrad.funiverseauthenservice.exception.ExpiredTokenException;
import com.unigrad.funiverseauthenservice.payload.TokenErrorMessage;
import com.unigrad.funiverseauthenservice.service.ITokenService;
import com.unigrad.funiverseauthenservice.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDuration;

    private final IUserService userService;

    private final ITokenService tokenService;

    public Token createRefreshToken(String personalMail) {

        Token token = new Token();

        //noinspection OptionalGetWithoutIsPresent
        token.setUser(userService.findByPersonalMail(personalMail).get());
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration));
        token.setToken(UUID.randomUUID().toString());

        tokenService.deleteTokensByUser_PersonalMailAndType(personalMail, Token.Type.REFRESH_TOKEN);

        return tokenService.save(token);
    }


    public Optional<Token> findByTokenAndType(String token, Token.Type type) {

        return tokenService.findByTokenAndType(token, type);
    }

    public Token verifyExpiration(Token token) throws ExpiredTokenException {

        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            tokenService.delete(token.getId());
            throw new ExpiredTokenException("Refresh token was expired. Please make a new login request!", TokenErrorMessage.TokenType.REFRESH_TOKEN);
        }

        return token;
    }

    @Transactional
    public void deleteByAccount(String eduMail) {

        tokenService.deleteTokensByUser_PersonalMailAndType(eduMail, Token.Type.REFRESH_TOKEN);
    }
}