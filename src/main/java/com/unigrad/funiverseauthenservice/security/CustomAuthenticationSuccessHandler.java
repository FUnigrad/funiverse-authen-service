package com.unigrad.funiverseauthenservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unigrad.funiverseauthenservice.entity.RefreshToken;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.payload.CustomOAuth2User;
import com.unigrad.funiverseauthenservice.payload.UserDTO;
import com.unigrad.funiverseauthenservice.security.jwt.JwtService;
import com.unigrad.funiverseauthenservice.security.services.RefreshTokenService;
import com.unigrad.funiverseauthenservice.service.IUserService;
import com.unigrad.funiverseauthenservice.service.IWorkspaceService;
import com.unigrad.funiverseauthenservice.util.DTOConverter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final IUserService userService;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final IWorkspaceService workspaceService;

    private final DTOConverter dtoConverter;

    public CustomAuthenticationSuccessHandler(IUserService userService, JwtService jwtService, RefreshTokenService refreshTokenService, IWorkspaceService workspaceService, DTOConverter dtoConverter) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.workspaceService = workspaceService;
        this.dtoConverter = dtoConverter;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getEmail();

        Optional<User> userOptional = userService.findByPersonalMail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String host = new URL(request.getRequestURL().toString()).getHost();
            String workspaceDomain = workspaceService.extractWorkspaceDomain(user, host);
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);

            String jwt = jwtService.generateToken(user);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
            UserDTO userDTO = dtoConverter.convert(refreshToken.getUser(), UserDTO.class);

            // Build the response body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", jwt);
            responseBody.put("user", userDTO);
            responseBody.put("workspaceDomain", workspaceDomain);

            // Write the response entity to the response
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);

            new ObjectMapper().writeValue(response.getOutputStream(), responseBody);

        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}