package com.unigrad.funiverseauthenservice.service.impl;

import com.unigrad.funiverseauthenservice.entity.Role;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.repository.IWorkspaceRepository;
import com.unigrad.funiverseauthenservice.service.IWorkspaceService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkspaceService implements IWorkspaceService {

    private final IWorkspaceRepository workspaceRepository;

    public WorkspaceService(IWorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public List<Workspace> getAll() {
        return workspaceRepository.findAll();
    }

    @Override
    public List<Workspace> getAllActive() {
        return workspaceRepository.findAllByActiveIsTrue();
    }

    @Override
    public Optional<Workspace> get(Long key) {
        return workspaceRepository.findById(key);
    }

    @Override
    public Workspace save(Workspace entity) {
        return workspaceRepository.save(entity);
    }

    @Override
    public void activate(Long key) {

    }

    @Override
    public void inactivate(Long key) {
        workspaceRepository.updateIsActive(key, false);
    }

    @Override
    public boolean isExist(Long key) {
        return workspaceRepository.existsById(key);
    }

    @Override
    public Optional<Workspace> findByDomain(String domain) {
        return workspaceRepository.findByDomainEqualsAndActiveTrue(domain);
    }

    @Override
    public String extractWorkspaceDomain(User userDetails, String host) {
        if (userDetails.getRole().equals(Role.SYSTEM_ADMIN)) {
            return "52.77.34.138:30002";
        }

        if (host.equals("localhost") || isLandingHost(host)) {
            return userDetails.getWorkspace().getDomain();
        }

        Optional<Workspace> workspaceOptional = findByDomain(host.split("\\.")[0]);

        if (workspaceOptional.isPresent()) {
            if (!workspaceOptional.get().getId().equals(userDetails.getWorkspace().getId())) {
                throw new UsernameNotFoundException("Email %s is not exist".formatted(userDetails.getUsername()));
            }
        }

        return userDetails.getWorkspace().getDomain();
    }

    private boolean isLandingHost(String host) {
        return host.split("\\.").length == 2;
    }

}