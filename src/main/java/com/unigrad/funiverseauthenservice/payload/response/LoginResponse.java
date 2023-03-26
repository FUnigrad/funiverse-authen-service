package com.unigrad.funiverseauthenservice.payload.response;

import com.unigrad.funiverseauthenservice.payload.UserDTO;
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

    private UserDTO user;

    private String refreshToken;

    private String workspaceDomain;
}