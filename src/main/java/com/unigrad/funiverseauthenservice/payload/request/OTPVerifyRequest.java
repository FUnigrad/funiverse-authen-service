package com.unigrad.funiverseauthenservice.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTPVerifyRequest {

    private String email;

    private String token;
}