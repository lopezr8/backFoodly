package com.rafael.foodly.foodly.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafael.foodly.foodly.entities.DishDescription;
import com.rafael.foodly.foodly.repositories.DishDescriptionRepository;

@Service
public class DishDescriptionServiceImpl implements DishDescriptionService{

    @Autowired
    private DishDescriptionRepository dishDescriptionRepository;

    @Override
    public DishDescription save(DishDescription dishDescription) {
        return dishDescriptionRepository.save(dishDescription);
    }

    @Override
    public Optional<DishDescription> findById(Integer id) {
        return dishDescriptionRepository.findById(id);
    }

}
