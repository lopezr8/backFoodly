package com.rafael.foodly.foodly.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.rafael.foodly.foodly.entities.Chef;
import com.rafael.foodly.foodly.entities.User;


public interface ChefRepository extends CrudRepository<Chef, Integer> {

    boolean existsByUser(User user);
    Optional<Chef> findByUser(User user);
    List<Chef> findByState(int state);
 
}
