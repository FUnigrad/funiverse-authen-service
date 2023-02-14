package com.unigrad.funiverseauthenservice.security.services;

import com.unigrad.funiverseauthenservice.entity.RefreshToken;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.payload.response.LoginResponse;
import com.unigrad.funiverseauthenservice.security.jwt.JwtService;
import com.unigrad.funiverseauthenservice.repository.IUserRepository;
import com.unigrad.funiverseauthenservice.payload.request.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final IUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(IUserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse register(RegisterRequest request) {

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .campusId((request.getCampusId()))
                .role(request.getRole())
                .build();

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userDetails = (User) authentication.getPrincipal();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return LoginResponse.builder()
                .accessToken(jwtToken)
                .user(new User(userDetails.getUsername(), userDetails.getCampusId(), userDetails.getRole()))
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
