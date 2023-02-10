package com.unigrad.funiverseauthenservice.security.services;

import com.unigrad.funiverseauthenservice.domain.RefreshToken;
import com.unigrad.funiverseauthenservice.exception.TokenRefreshException;
import com.unigrad.funiverseauthenservice.repository.IRefreshTokenRepository;
import com.unigrad.funiverseauthenservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;


    public RefreshToken createRefreshToken(String accountName) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findByUsername(accountName).get());
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
    public void deleteByAccount(String username) {
        refreshTokenRepository.deleteRefreshTokensByUser_Username(username);
    }
}