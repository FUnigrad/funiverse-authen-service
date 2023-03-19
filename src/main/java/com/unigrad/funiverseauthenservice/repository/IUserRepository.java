package com.unigrad.funiverseauthenservice.repository;

import com.unigrad.funiverseauthenservice.entity.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUserRepository extends IBaseRepository<User, Long> {

    @Query(value = "select u from User u where u.eduMail = :eduMail and u.isActive = true")
    Optional<User> findByEduMail(String eduMail);
}