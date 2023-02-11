package com.unigrad.funiverseauthenservice.payload.response;

import com.unigrad.funiverseauthenservice.domain.User;
import com.unigrad.funiverseauthenservice.security.services.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;

//    private final String type = "Bearer";

    private String refreshToken;

    private User user;
}