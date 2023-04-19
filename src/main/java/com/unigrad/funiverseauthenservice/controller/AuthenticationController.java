package com.unigrad.funiverseauthenservice.controller;


import com.unigrad.funiverseauthenservice.entity.Role;
import com.unigrad.funiverseauthenservice.entity.Token;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.exception.ExpiredTokenException;
import com.unigrad.funiverseauthenservice.exception.InvalidRefreshTokenException;
import com.unigrad.funiverseauthenservice.payload.TokenErrorMessage;
import com.unigrad.funiverseauthenservice.payload.UserDTO;
import com.unigrad.funiverseauthenservice.payload.request.*;
import com.unigrad.funiverseauthenservice.payload.response.LoginResponse;
import com.unigrad.funiverseauthenservice.payload.response.TokenRefreshResponse;
import com.unigrad.funiverseauthenservice.security.jwt.JwtService;
import com.unigrad.funiverseauthenservice.security.services.RefreshTokenService;
import com.unigrad.funiverseauthenservice.service.IEmailService;
import com.unigrad.funiverseauthenservice.service.ITokenService;
import com.unigrad.funiverseauthenservice.service.IUserService;
import com.unigrad.funiverseauthenservice.service.IWorkspaceService;
import com.unigrad.funiverseauthenservice.util.CookieUtils;
import com.unigrad.funiverseauthenservice.util.DTOConverter;
import com.unigrad.funiverseauthenservice.util.OTPUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final IUserService userService;

    private final IWorkspaceService workspaceService;

    private final IEmailService emailService;

    private final ITokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    private final DTOConverter dtoConverter;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User userDetails = (User) authentication.getPrincipal();
        String workspaceDomain = Role.SYSTEM_ADMIN.equals(userDetails.getRole()) ? "system.funiverse.com" : userDetails.getWorkspace().getDomain();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtService.generateToken(userDetails);

        Token token = refreshTokenService.createRefreshToken(userDetails.getUsername());

        CookieUtils.addCookie(response, "refresh-token", token.getToken(), 600000000);

        return ResponseEntity.ok(new LoginResponse(
                jwt,
                dtoConverter.convert(userDetails, UserDTO.class),
                token.getToken(),
                workspaceDomain));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {

        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByTokenAndType(requestRefreshToken, Token.Type.REFRESH_TOKEN)
                .map(refreshTokenService::verifyExpiration)
                .map(Token::getUser)
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

        refreshTokenService.deleteByAccount(logOutRequest.getEmail());
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

    @GetMapping("/reset-password")
    public ResponseEntity<String> sendMailResetPassword(@RequestParam String email) {
        Optional<User> userOptional = userService.findByPersonalMail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User with email %s is not exist".formatted(email));
        }

        Token token = OTPUtil.generate(userOptional.get());

        try {
            tokenService.deleteTokensByUser_PersonalMailAndType(userOptional.get().getPersonalMail(), Token.Type.OTP);
            tokenService.save(token);
            emailService.sendResetPasswordEmail(token);
        } catch (MessagingException | UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        return ResponseEntity.ok(token.getToken());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<LoginResponse> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletResponse response) {
        Optional<User> userOptional = userService.findByPersonalMail(resetPasswordRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User with email %s is not exist".formatted(resetPasswordRequest.getEmail()));
        }

        userOptional.get().setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userService.save(userOptional.get());
        tokenService.deleteTokensByUser_PersonalMailAndType(userOptional.get().getPersonalMail(), Token.Type.OTP);

        return login(new LoginRequest(resetPasswordRequest.getEmail(), resetPasswordRequest.getPassword()), response);
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyOTP(@RequestBody OTPVerifyRequest otpVerifyRequest) {
        Optional<Token> tokenOptional = tokenService.findByTokenAndType(otpVerifyRequest.getToken(), Token.Type.OTP);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (tokenOptional.get().getUser().getPersonalMail().equals(otpVerifyRequest.getEmail())) {
            if (OTPUtil.isExpired(tokenOptional.get())) {
                throw new ExpiredTokenException("Token is expired", TokenErrorMessage.TokenType.OTP);
            }

            tokenService.delete(tokenOptional.get().getId());
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

}