package com.unigrad.funiverseauthenservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface IBaseRepository<T, K> extends JpaRepository<T, K>, JpaSpecificationExecutor<T> {

    @Modifying
    @Query("update #{#entityName} t set t.isActive = :isActive where t.id = :key")
    void updateIsActive(K key, boolean isActive);

    @Query("select t from #{#entityName} t where t.isActive = true")
    List<T> findAllByActiveIsTrue();
}