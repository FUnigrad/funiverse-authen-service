package com.unigrad.funiverseauthenservice.repository;

import com.unigrad.funiverseauthenservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByUsername(String username);

}
