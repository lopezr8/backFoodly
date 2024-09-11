package com.rafael.foodly.foodly.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.rafael.foodly.foodly.entities.Dish;
import com.rafael.foodly.foodly.entities.User;



public interface DishRepository extends CrudRepository<Dish, Integer> {
    Optional<Dish> findById(Integer id);

    List<Dish> findByChef(User cheg);

}
