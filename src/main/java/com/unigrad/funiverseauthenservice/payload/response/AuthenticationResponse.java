package com.unigrad.funiverseauthenservice.payload.response;

import com.unigrad.funiverseauthenservice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
  private String accessToken;

  private final String type = "Bearer";

  private String refreshToken;

  private User user;
}
