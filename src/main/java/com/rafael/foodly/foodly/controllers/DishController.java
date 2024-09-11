package com.rafael.foodly.foodly.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rafael.foodly.foodly.entities.Chef;
import com.rafael.foodly.foodly.entities.Dish;
import com.rafael.foodly.foodly.entities.User;
import com.rafael.foodly.foodly.services.ChefService;
import com.rafael.foodly.foodly.services.DishService;
import com.rafael.foodly.foodly.services.UserService;

import jakarta.validation.Valid;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/foodly/chef/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    
    @Autowired 
    private UserService userService;

    @Autowired
    private ChefService chefService;

    @GetMapping
    public List<Dish> list(){
        return dishService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewDish (@PathVariable int id){
        Optional<Dish> dishOptional =  dishService.findById(id);
        if(dishOptional.isPresent()){
            return ResponseEntity.ok(dishOptional.orElseThrow() );
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/list/{username}")
    public ResponseEntity<?> viewDishChef (@PathVariable String username){
        Optional<User> optionalUser = userService.findByUsername(username);
        if(optionalUser.isPresent()){
            Optional<Chef> optionalChef =  chefService.findByUser(optionalUser.orElseThrow());
            if(optionalChef.isPresent()){
                List<Dish> dishList = dishService.findByChef(optionalUser.orElseThrow());
                return ResponseEntity.ok(dishList);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> create(@Valid @RequestBody Dish dish, BindingResult result, @PathVariable String username){
        if(result.hasFieldErrors()){
            return validation(result);
        }
        
        if(!validateUser(username)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
        }

        Optional<User> optionalUser = userService.findByUsername(username);
        if(optionalUser.isPresent()){
            Optional<Chef> optionalChef =  chefService.findByUser(optionalUser.orElseThrow());
            if(optionalChef.isPresent()){
                if(optionalChef.orElseThrow().getState() == 0){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Chef is inactive ");
                }

                dish.setChef(optionalUser.orElseThrow());

                Dish dishCreated = dishService.save(dish);    
                
                return ResponseEntity.status(HttpStatus.CREATED).body(dishCreated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chef not found");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        
    }

    @PutMapping("/{username}/{id}")
    public ResponseEntity<?> edit(@RequestBody Dish dish, BindingResult result, @PathVariable int id, @PathVariable String username){

        if(result.hasFieldErrors()){
            return validation(result);
        }
        
        if(!validateUser(username)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
        }
        
        return EditDeleteCaseValidation(username, id, 1, dish);
        
    }


    @DeleteMapping("/{username}/{id}")
    @ResponseBody
    public ResponseEntity<?> delete (@PathVariable String username,@PathVariable Integer id){

        if(!validateUser(username)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
        }

        return EditDeleteCaseValidation(username, id, 0, null);
        
    }

    public ResponseEntity<?> EditDeleteCaseValidation(String username, int id, int type, Dish dishEdit){
        Optional<User> optionalUser = userService.findByUsername(username);
        if(optionalUser.isPresent()){
            Optional<Chef> optionalChef =  chefService.findByUser(optionalUser.orElseThrow());
            if(optionalChef.isPresent()){
                if(optionalChef.orElseThrow().getState() == 0){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Chef is inactive ");
                }
                Optional<Dish> dishOptional =  dishService.findById(id);
                if(dishOptional.isPresent()){
                    Dish dish = dishOptional.orElseThrow();
                    if(dish.getChef().getId_user() != optionalChef.orElseThrow().getUser().getId_user()){
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Chef mismatch ");
                    }
                    if (type == 0 ){
                        dishService.delete(id);
                        return ResponseEntity.status(HttpStatus.OK).body(dish);
                    }
                    dishService.update(id,dishEdit);
                    return ResponseEntity.status(HttpStatus.OK).body(dish);

                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dish not found");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chef not found");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

        public boolean validateUser (String username){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();

            if (!username.equals(authenticatedUsername)) {
                return false;
            }
            return true;
        }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors); 
    }

}
