package com.unigrad.funiverseauthenservice.service;

import com.unigrad.funiverseauthenservice.entity.User;

import java.util.Optional;

public interface IUserService extends IBaseService<User, Long> {

    Optional<User> findByMail(String mail);
}