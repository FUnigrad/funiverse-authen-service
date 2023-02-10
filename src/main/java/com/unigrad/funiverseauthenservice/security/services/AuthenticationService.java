package com.unigrad.funiverseauthenservice.security.services;


import com.unigrad.funiverseauthenservice.domain.RefreshToken;
import com.unigrad.funiverseauthenservice.payload.request.LoginRequest;
import com.unigrad.funiverseauthenservice.security.jwt.JwtService;
import com.unigrad.funiverseauthenservice.domain.User;
import com.unigrad.funiverseauthenservice.repository.UserRepository;
import com.unigrad.funiverseauthenservice.payload.request.AuthenticationRequest;
import com.unigrad.funiverseauthenservice.payload.request.RegisterRequest;
import com.unigrad.funiverseauthenservice.payload.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;

  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .tenantId((request.getTenantId()))
        .build();

    repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User userDetails = (User) authentication.getPrincipal();
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .user(user)
            .refreshToken(refreshToken.getToken())
        .build();
  }
}
