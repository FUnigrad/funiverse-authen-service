package com.unigrad.funiverseauthenservice.payload.response;

import com.unigrad.funiverseauthenservice.entity.User;
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

    private String refreshToken;

    private UserDTO user;
}