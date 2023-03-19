package com.unigrad.funiverseauthenservice.repository;

import com.unigrad.funiverseauthenservice.entity.Workspace;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IWorkspaceRepository extends IBaseRepository<Workspace, Long> {

    @Query(value = "select w from Workspace w where w.domain = :domain and w.isActive = true")
    Optional<Workspace> findByDomainEqualsAndActiveTrue(String domain);
}