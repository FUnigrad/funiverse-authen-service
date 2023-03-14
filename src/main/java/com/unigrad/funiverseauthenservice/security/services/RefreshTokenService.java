package com.unigrad.funiverseauthenservice.security.services;

import com.unigrad.funiverseauthenservice.entity.RefreshToken;
import com.unigrad.funiverseauthenservice.exception.TokenRefreshException;
import com.unigrad.funiverseauthenservice.repository.IRefreshTokenRepository;
import com.unigrad.funiverseauthenservice.repository.IUserRepository;
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

    private final IUserRepository userRepository;

    private final IRefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(IUserRepository userRepository, IRefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String accountName) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findByEduMail(accountName).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token) {

        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new login request!");
        }

        return token;
    }

    @Transactional
    public void deleteByAccount(String eduMail) {

        refreshTokenRepository.deleteRefreshTokensByUser_EduMail(eduMail);
    }
}