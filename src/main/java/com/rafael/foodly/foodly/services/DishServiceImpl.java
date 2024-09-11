package com.rafael.foodly.foodly.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafael.foodly.foodly.entities.Dish;
import com.rafael.foodly.foodly.entities.DishDescription;
import com.rafael.foodly.foodly.entities.User;
import com.rafael.foodly.foodly.repositories.DishRepository;

@Service
public class DishServiceImpl implements DishService{

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private DishDescriptionService dishDescriptionService;

    @Override
    @Transactional(readOnly = true)
    public List<Dish> findAll() {
        return (List<Dish>) dishRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Dish> findById(Integer id) {
        return dishRepository.findById(id);
    }

    @Override
    public Dish save(Dish dish) {
        Dish dishDb = dishRepository.save(dish);

        dish.getDescriptions().forEach(dishDescription -> {
            dishDescription.setDish(dishDb);
            dishDescriptionService.save(dishDescription);
        });
        return dishDb;
    }

    @Override
    public void delete(Integer id) {
        dishRepository.deleteById(id);
    }

    @Override
    public Optional<Dish> update(Integer id, Dish dish) {
        Optional<Dish> dishOptional = dishRepository.findById(id);
        if(dishOptional.isPresent()){
            Dish dishDB = dishOptional.orElseThrow();
        
            if(dish.getDescriptions() != null){
               dish.getDescriptions().forEach( dishDescription ->{
                    Optional<DishDescription> dishDoptional = dishDescriptionService.findById(dishDescription.getId());
                    if(dishDoptional.isPresent()){
                        DishDescription dishDescriptionDB = dishDoptional.orElseThrow();
                        dishDescriptionDB.setElement(dishDescription.getElement());
                        dishDescriptionDB.setGr(dishDescription.getGr());
                    }
               }); 
            }
            dishDB.setUnits(dish.getUnits());
            dishDB.setValue(dish.getValue());
            dishDB.setName(dish.getName());
            dishDB.setImage(dish.getImage());
            
            
            return Optional.of(dishRepository.save(dishDB));
        }
        

        return dishOptional;
    }

    @Override
    public List<Dish> findByChef(User chef) {
        return dishRepository.findByChef(chef);
    }

    

}
