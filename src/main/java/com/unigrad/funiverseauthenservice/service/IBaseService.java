package com.unigrad.funiverseauthenservice.service;

import java.util.List;
import java.util.Optional;

public interface IBaseService<T, K>{

    List<T> getAll();

    List<T> getAllActive();

    Optional<T> get(K key);

    T save(T entity);

    void activate(K key);

    void inactivate(K key);

    boolean isExist(K key);
}