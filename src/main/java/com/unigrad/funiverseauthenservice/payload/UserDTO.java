package com.unigrad.funiverseauthenservice.payload;

import com.unigrad.funiverseauthenservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String code;

    private String workspaceCode;

    private Role role;
}