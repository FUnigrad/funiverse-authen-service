package com.unigrad.funiverseauthenservice.repository;

import com.unigrad.funiverseauthenservice.entity.User;

import java.util.Optional;

public interface IUserRepository extends IBaseRepository<User, Long> {

    Optional<User> findByEduMail(String eduMail);
}