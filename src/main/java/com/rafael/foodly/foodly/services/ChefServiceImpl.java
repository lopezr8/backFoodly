package com.rafael.foodly.foodly.services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafael.foodly.foodly.entities.Chef;
import com.rafael.foodly.foodly.entities.User;
import com.rafael.foodly.foodly.repositories.ChefRepository;

@Service
public class ChefServiceImpl implements ChefService {

    @Autowired
    private ChefRepository chefRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Chef> findAll() {
        return (List<Chef>) chefRepository.findByState(1);
    }

    @Override
    public Chef save(Chef chef) {
        return chefRepository.save(chef);
    }

    @Override
    public boolean existsByUser(User user) {
        return chefRepository.existsByUser(user);
    }

    @Override
    public Optional<Chef> findByUser(User user) {
        return chefRepository.findByUser(user);
    }

    @Override
    public Optional<Chef> delete(User user) {
        Optional<Chef> optionalChef = chefRepository.findByUser(user);
        if(optionalChef.isPresent()){
            Chef chefDB = optionalChef.orElseThrow();
            chefDB.setState(0);
            return Optional.of(chefRepository.save(chefDB));
        }
        return optionalChef;
    }

    @Override
    public Optional<Chef> activate(User user) {
        Optional<Chef> chefOptional = chefRepository.findByUser(user);
        if(chefOptional.isPresent()){
            chefOptional.orElseThrow().setState(1);
            return  Optional.of( chefRepository.save(chefOptional.orElseThrow()));
        }
        return chefOptional;
    }



}
