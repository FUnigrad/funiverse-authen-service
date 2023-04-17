package com.unigrad.funiverseauthenservice.util;

import com.unigrad.funiverseauthenservice.entity.Token;
import com.unigrad.funiverseauthenservice.entity.User;

import java.time.Instant;
import java.util.Random;

public class OTPUtil {

    private static final Long EXPIRATION_SECOND = 5L * 60;

    public static Token generate(User user) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        return Token.builder()
                .token(otp)
                .type(Token.Type.OTP)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(EXPIRATION_SECOND))
                .build();
    }

    public static boolean isExpired(Token token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}