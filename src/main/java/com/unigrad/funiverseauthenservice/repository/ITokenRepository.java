package com.unigrad.funiverseauthenservice.repository;

import com.unigrad.funiverseauthenservice.entity.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface ITokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByTokenAndType(String token, Token.Type type);

    @Modifying
    @Transactional
    void deleteTokensByUser_PersonalMailAndType(String username, Token.Type type);
}