package com.unigrad.funiverseauthenservice.repository;

import com.unigrad.funiverseauthenservice.security.services.UserDetailsImpl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDetailsImpl, String> {

  Optional<UserDetailsImpl> findByUsername(String username);

}
