package com.unigrad.funiverseauthenservice.domain;

import com.unigrad.funiverseauthenservice.security.services.UserDetailsImpl;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn
    private UserDetailsImpl user;

    private String token;

    private Instant expiryDate;
}