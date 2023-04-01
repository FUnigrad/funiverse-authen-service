package com.unigrad.funiverseauthenservice.repository;

import com.unigrad.funiverseauthenservice.entity.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends IBaseRepository<User, Long> {

    @Query(value = "select u from User u where u.personalMail = :personalMail and u.isActive = true")
    Optional<User> findByPersonalMail(String personalMail);

    @Query(value = "select u from User u where u.isActive and u.workspace.id = :workspaceId and u.role = 'WORKSPACE_ADMIN'")
    Optional<User> findWorkspaceAdmin(Long workspaceId);

    List<User> findAllByWorkspaceId(Long id);
}