package com.unigrad.funiverseauthenservice.service;

import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import org.springframework.stereotype.Service;

@Service
public interface IAppCommunicateService {
    boolean saveUser(User user, String domain, String token);

    boolean saveWorkspace(Workspace workspace, String domain, String token);
}