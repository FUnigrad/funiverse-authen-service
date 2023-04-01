package com.unigrad.funiverseauthenservice.service;

import com.unigrad.funiverseauthenservice.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService extends IBaseService<User, Long> {

    Optional<User> findByPersonalMail(String mail);

    Optional<User> findWorkspaceAdmin(Long workspaceId);

    List<User> findAllByWorkspaceId(Long id);

    void delete(Long id);
}