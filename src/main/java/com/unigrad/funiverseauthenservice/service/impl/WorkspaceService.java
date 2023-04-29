package com.unigrad.funiverseauthenservice.service.impl;

import com.unigrad.funiverseauthenservice.entity.Role;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.repository.IWorkspaceRepository;
import com.unigrad.funiverseauthenservice.service.IUserService;
import com.unigrad.funiverseauthenservice.service.IWorkspaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements IWorkspaceService {

    private final IWorkspaceRepository workspaceRepository;

    private final IUserService userService;

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
    @Transactional
    public void activate(Long key) {
        workspaceRepository.updateIsActive(key, true);
    }

    @Override
    @Transactional
    public void inactivate(Long key) {
        List<User> users = userService.findAllByWorkspaceId(key);

        users.forEach(user -> userService.delete(user.getId()));
        workspaceRepository.deleteById(key);
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
            return "admin.funiverse.world";
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

    @Override
    public boolean isDomainExist(String domain) {
        return workspaceRepository.isDomainExist(domain);
    }

    private boolean isLandingHost(String host) {
        return host.split("\\.").length == 2;
    }

}