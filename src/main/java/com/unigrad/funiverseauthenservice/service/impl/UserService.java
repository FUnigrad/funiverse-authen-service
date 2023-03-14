package com.unigrad.funiverseauthenservice.service.impl;

import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.repository.IUserRepository;
import com.unigrad.funiverseauthenservice.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllActive() {
        return userRepository.findAllByActiveIsTrue();
    }

    @Override
    public Optional<User> get(Long key) {
        return userRepository.findById(key);
    }

    @Override
    public User save(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public void activate(Long key) {

    }

    @Override
    public void inactivate(Long key) {
        userRepository.updateIsActive(key, false);
    }

    @Override
    public boolean isExist(Long key) {
        return false;
    }
}