package com.unigrad.funiverseauthenservice.controller;


import com.unigrad.funiverseauthenservice.entity.RefreshToken;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.exception.InvalidRefreshTokenException;
import com.unigrad.funiverseauthenservice.payload.UserDTO;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.payload.request.ChangePasswordRequest;
import com.unigrad.funiverseauthenservice.payload.request.LogOutRequest;
import com.unigrad.funiverseauthenservice.payload.request.LoginRequest;
import com.unigrad.funiverseauthenservice.payload.request.MailCheckRequest;
import com.unigrad.funiverseauthenservice.payload.request.TokenRefreshRequest;
import com.unigrad.funiverseauthenservice.payload.response.LoginResponse;
import com.unigrad.funiverseauthenservice.payload.response.TokenRefreshResponse;
import com.unigrad.funiverseauthenservice.security.jwt.JwtService;
import com.unigrad.funiverseauthenservice.security.services.RefreshTokenService;
import com.unigrad.funiverseauthenservice.service.IUserService;
import com.unigrad.funiverseauthenservice.service.IWorkspaceService;
import com.unigrad.funiverseauthenservice.util.DTOConverter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@RestController
@RequestMapping("/oauth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final IUserService userService;

    private final IWorkspaceService workspaceService;

    private final PasswordEncoder passwordEncoder;

    private final DTOConverter dtoConverter;

    public AuthenticationController(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, JwtService jwtService, IUserService userService, PasswordEncoder passwordEncoder, IWorkspaceService workspaceService, DTOConverter dtoConverter) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.workspaceService = workspaceService;
        this.dtoConverter = dtoConverter;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        try {
            String host = new URL(request.getRequestURL().toString()).getHost();
            User userDetails = (User) authentication.getPrincipal();
            String workspaceDomain = workspaceService.extractWorkspaceDomain(userDetails, host);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtService.generateToken(userDetails);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

            Cookie cookie = new Cookie("refresh-token", refreshToken.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);

            return ResponseEntity.ok(new LoginResponse(
                    jwt,
                    dtoConverter.convert(userDetails, UserDTO.class),
                    workspaceDomain));

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {

        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(token));
                })
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Refresh token is invalid"
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestBody LogOutRequest logOutRequest) {

        refreshTokenService.deleteByAccount(logOutRequest.getAccountName());
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok("Log out successful!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userMail = authentication.getName();
        Optional<User> userOptional = userService.findByPersonalMail(userMail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

                userService.save(user);
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("verify-mail")
    public ResponseEntity<Workspace> checkEmail(@RequestBody MailCheckRequest mailCheckRequest) {
        Optional<User> userOptional = userService.findByPersonalMail(mailCheckRequest.getEmail());

        if (mailCheckRequest.getWorkspaceId() == null) {
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get().getWorkspace());
            }

        } else {
            Optional<Workspace> workspaceOptional = workspaceService.get(mailCheckRequest.getWorkspaceId());

            if (workspaceOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (userOptional.isPresent()) {
                if (userOptional.get().getWorkspace().getId().equals(workspaceOptional.get().getId())) {
                    return ResponseEntity.ok(workspaceOptional.get());
                }
            }
        }
        throw new UsernameNotFoundException("Email %s not exist".formatted(mailCheckRequest.getEmail()));
    }
}