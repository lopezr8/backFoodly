package com.rafael.foodly.foodly.services;

import java.util.Optional;

import com.rafael.foodly.foodly.entities.DishDescription;

public interface DishDescriptionService {
    DishDescription save(DishDescription dishDescription);

    Optional<DishDescription> findById(Integer id);
}
