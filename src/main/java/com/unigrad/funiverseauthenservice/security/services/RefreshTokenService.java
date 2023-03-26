package com.unigrad.funiverseauthenservice.security.services;

import com.unigrad.funiverseauthenservice.entity.RefreshToken;
import com.unigrad.funiverseauthenservice.exception.ExpiredTokenException;
import com.unigrad.funiverseauthenservice.payload.TokenErrorMessage;
import com.unigrad.funiverseauthenservice.repository.IRefreshTokenRepository;
import com.unigrad.funiverseauthenservice.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDuration;

    private final IUserService userService;

    private final IRefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(IUserService userService, IRefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String personalMail) {

        RefreshToken refreshToken = new RefreshToken();

        //noinspection OptionalGetWithoutIsPresent
        refreshToken.setUser(userService.findByPersonalMail(personalMail).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenRepository.deleteRefreshTokensByUser_PersonalMail(personalMail);

        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token) {

        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) throws ExpiredTokenException {

        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new ExpiredTokenException("Refresh token was expired. Please make a new login request!", TokenErrorMessage.TokenType.REFRESH_TOKEN);
        }

        return token;
    }

    @Transactional
    public void deleteByAccount(String eduMail) {

        refreshTokenRepository.deleteRefreshTokensByUser_PersonalMail(eduMail);
    }
}