package com.unigrad.funiverseauthenservice.repository;

import com.unigrad.funiverseauthenservice.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}