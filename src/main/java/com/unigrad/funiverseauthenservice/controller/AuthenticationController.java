package com.unigrad.funiverseauthenservice.controller;


import com.unigrad.funiverseauthenservice.entity.RefreshToken;
import com.unigrad.funiverseauthenservice.payload.UserDTO;
import com.unigrad.funiverseauthenservice.payload.request.LogOutRequest;
import com.unigrad.funiverseauthenservice.payload.request.LoginRequest;
import com.unigrad.funiverseauthenservice.payload.request.RegisterRequest;
import com.unigrad.funiverseauthenservice.payload.request.TokenRefreshRequest;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.exception.TokenRefreshException;
import com.unigrad.funiverseauthenservice.payload.response.LoginResponse;
import com.unigrad.funiverseauthenservice.payload.response.TokenRefreshResponse;
import com.unigrad.funiverseauthenservice.security.jwt.JwtService;
import com.unigrad.funiverseauthenservice.security.services.AuthenticationService;
import com.unigrad.funiverseauthenservice.security.services.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationService service, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userDetails = (User) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        return ResponseEntity.ok(new LoginResponse(
                jwt,
                refreshToken.getToken(),
                new UserDTO(userDetails.getUsername(), userDetails.getWorkspace().getCode(), userDetails.getRole())));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {

        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(
                        requestRefreshToken,
                        "Refresh token is not in database!"
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestBody LogOutRequest logOutRequest) {

        refreshTokenService.deleteByAccount(logOutRequest.getAccountName());
        return ResponseEntity.ok("Log out successful!");
    }
}