package com.rafael.foodly.foodly.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.rafael.foodly.foodly.entities.User;


public interface UserRepository extends CrudRepository<User, Integer> {
    boolean existsByIdentification(String identification);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    List<User> findByState(int state);

}
