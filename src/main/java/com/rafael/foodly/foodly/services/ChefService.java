package com.rafael.foodly.foodly.services;


import java.util.List;
import java.util.Optional;

import com.rafael.foodly.foodly.entities.Chef;
import com.rafael.foodly.foodly.entities.User;


public interface ChefService {

    Chef save(Chef chef);

    boolean existsByUser(User user);

    Optional<Chef> findByUser(User user);

    Optional<Chef> delete(User user);

    Optional<Chef> activate (User user);

    List<Chef> findAll();

}
