package com.unigrad.funiverseauthenservice.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceCreateResponse {

    private Long id;

    private String name;

    private String code;

    private String domain;

    private Admin admin;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Admin {

        private Long id;

        private String personalMail;

        private String eduMail;
    }
}