package com.unigrad.funiverseauthenservice.security.oauth2;

import com.unigrad.funiverseauthenservice.entity.RefreshToken;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.payload.CustomOAuth2User;
import com.unigrad.funiverseauthenservice.security.jwt.JwtService;
import com.unigrad.funiverseauthenservice.security.services.RefreshTokenService;
import com.unigrad.funiverseauthenservice.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

//@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final IUserService userService;

    private final RefreshTokenService refreshTokenService;

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);


    private final String LANDING_PAGE_URL = "http://funiverse.world";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getEmail();

        Optional<User> userOptional = userService.findByPersonalMail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);

            String jwt = jwtService.generateToken(user);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

//            CookieUtils.addCookie(response, "refreshToken", refreshToken.getToken(), 604800);
//            CookieUtils.addCookie(response, "accessToken", jwt, 172800);

            String targetUrl = UriComponentsBuilder.fromUriString(LANDING_PAGE_URL)
                    .queryParam("refreshToken", refreshToken.getToken())
                    .queryParam("accessToken", jwt)
                    .build().toUriString();

            clearAuthenticationAttributes(request, response);
            logger.info("login success");
            logger.info("redirect to %s".formatted(targetUrl));
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } else {
            httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
            logger.info("login failed, user not exist");

            String targetUrl = UriComponentsBuilder.fromUriString(LANDING_PAGE_URL)
                    .queryParam("error", "badcridential")
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}