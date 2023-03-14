package com.unigrad.funiverseauthenservice.service.impl;

import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.repository.IWorkspaceRepository;
import com.unigrad.funiverseauthenservice.service.IWorkspaceService;
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
}