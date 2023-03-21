package com.unigrad.funiverseauthenservice.payload.request;

import com.unigrad.funiverseauthenservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceCreateRequest {

    private String name;

    private String code;

    private String domain;

    private String personalMail;

    private String eduMail;
}