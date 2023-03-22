package com.unigrad.funiverseauthenservice.service;

import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;

import java.util.Optional;

public interface IWorkspaceService extends IBaseService<Workspace, Long> {

    Optional<Workspace> findByDomain(String domain);

    String extractWorkspaceDomain(User userDetails, String host);

    boolean isDomainExist(String domain);
}