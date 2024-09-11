package com.rafael.foodly.foodly.services;

import java.util.List;
import java.util.Optional;

import com.rafael.foodly.foodly.entities.Dish;
import com.rafael.foodly.foodly.entities.User;

public interface DishService {

    Dish save(Dish dish);

    List<Dish> findAll();

    Optional<Dish> findById(Integer id);

    void delete(Integer id);

    Optional<Dish> update(Integer id, Dish dish);

    List<Dish> findByChef(User chef);
}
