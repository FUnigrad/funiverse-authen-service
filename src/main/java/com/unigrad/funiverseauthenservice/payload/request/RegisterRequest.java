package com.unigrad.funiverseauthenservice.payload.request;

import com.unigrad.funiverseauthenservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String username;

    private String password;

    private String campusId;

    private Role role;
}
