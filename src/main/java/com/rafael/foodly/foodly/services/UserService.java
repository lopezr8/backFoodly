package com.rafael.foodly.foodly.services;

import java.util.List;
import java.util.Optional;

import com.rafael.foodly.foodly.entities.User;

public interface UserService {

    List<User> findAll();

    List<User> findByState(Integer state);

    Optional<User> findById(Integer id);

    Optional<User> activate(String username);

    Optional<User> findByUsername(String username);

    User save(User user);

    Optional<User> update(Integer id, User user);

    Optional<User> delete(String username);

    boolean existsByIdentification(String identification);

    boolean existsByUsername(String username);
    

}
