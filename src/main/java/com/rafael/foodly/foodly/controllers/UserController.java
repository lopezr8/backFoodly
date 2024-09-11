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
import com.rafael.foodly.foodly.entities.Customer;
import com.rafael.foodly.foodly.entities.User;
import com.rafael.foodly.foodly.services.ChefService;
import com.rafael.foodly.foodly.services.CustomerService;
import com.rafael.foodly.foodly.services.UserService;


import jakarta.validation.Valid;

//TODO: 
//?1. no se puede eliminar por completo, porque se jode el checkout, por eso se va a cambiar el estado a inactivo
//?1. get de los usuarios que esten en activo 
//?1. get de los chefs 
//?1. get de los clientes

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/foodly/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChefService chefService;

    @Autowired
    private CustomerService customerService;
    

    @GetMapping
    public List<User> list(){
        return userService.findAll();
    }

    @GetMapping("/active")
    public List<User> listActive(){
        return userService.findByState(1);
    }

    @GetMapping("/ROLE_CHEFStatus/{id}")
    public boolean checkStateChef(@PathVariable String id){
         User user = userService.findByUsername(id).orElseThrow();
         Chef chef = chefService.findByUser(user).orElseThrow();
         if ( chef.getState() == 1) return true ;
         return false;
    }
    @GetMapping("/ROLE_CUSTOMERStatus/{id}")
    public boolean checkStateCustomer(@PathVariable String id){
         User user = userService.findByUsername(id).orElseThrow();
         Customer customer = customerService.findByUser(user).orElseThrow();
         if ( customer.getState() == 1) return true ;
         return false;

    }

    @GetMapping("/identification/{id}")
    public boolean checkId(@PathVariable String id){
        return userService.existsByIdentification(id);
    }

    @GetMapping("/username/{id}")
    public boolean checkusername(@PathVariable String id){
        return userService.existsByUsername(id);
    }


    @GetMapping("/inactive")
    public List<User> listInactive(){
        return userService.findByState(0);
    }

    @GetMapping("/chefs")
    public List<Chef> listChefs(){
        return chefService.findAll();
    }

    @GetMapping("/customers")
    public List<Customer> listCustomers(){
        return customerService.findAll();
    }

    @GetMapping("/chef/{username}")
    public ResponseEntity<?> getChef (@PathVariable String username ){
        

        Optional<User> userOptional =  userService.findByUsername(username);
        if(userOptional.isPresent()){


            Optional<Chef> chefOptional = chefService.findByUser(userOptional.orElseThrow());
            if(chefOptional.isPresent()){
                return ResponseEntity.ok(chefOptional.orElseThrow());
            }
            
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        
    }

    @GetMapping("/customer/{username}")
    public ResponseEntity<?> getCustomer (@PathVariable String username ){
        

        Optional<User> userOptional =  userService.findByUsername(username);
        if(userOptional.isPresent()){

            Optional<Customer> customerOptional = customerService.findByUser(userOptional.orElseThrow());
            if(customerOptional.isPresent()){
                return ResponseEntity.ok(customerOptional.orElseThrow());
            }
            
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> viewUsername (@PathVariable String username){
        Optional<User> userOptional =  userService.findByUsername(username);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(userOptional.orElseThrow() );
        }
        return ResponseEntity.notFound().build();
    }  

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result){
        if(result.hasFieldErrors()){
            return validation(result);
        }

        User userCreated = userService.save(user);    
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    
    @PostMapping("/chef")
    public ResponseEntity<?> createChef(@Valid @RequestBody User user, BindingResult result){
        if(result.hasFieldErrors()){
            return validation(result);
        }
        user.setType(1);
        
        User userCreated = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);    
        
    }
    
    @PostMapping("/customer")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody User user, BindingResult result){
        if(result.hasFieldErrors()){
            return validation(result);
        }
        
        user.setType(0);
        
        User userCreated = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);    
    
    }

    @PutMapping("/chef-customer") //esta se va a usar cuando el usuario este logeado, es decir desde adentro, necesita el type 
    public ResponseEntity<?> updateChefOrCustomer( @RequestBody User user, BindingResult result){
        if(result.hasFieldErrors()){
            return validation(result);
        }
        
        Optional<User> userOptional =  userService.findByUsername(user.getUsername());
        if(userOptional.isPresent()){

            if(!validateUser(user.getUsername())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
            }

            
            return updateUserType(userOptional.orElseThrow().getId_user() ,user);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> update(  @RequestBody User user, BindingResult result, @PathVariable Integer id){
        
        
        if(result.hasFieldErrors()){
            return validation(result);
        }
        Optional<User> userOptional = userService.update(id,user);    
        if(userOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(userOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();

    } 


    @PutMapping("/activate/chef/{username}")
    public ResponseEntity<?> activateChef(@PathVariable String username){
        Optional<User> userOptional = userService.findByUsername(username);
        if(userOptional.isPresent()){
            Optional<Chef> chefOptional = chefService.activate(userOptional.orElseThrow());
            if(chefOptional.isPresent()){
                return ResponseEntity.ok(chefOptional.orElseThrow());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/activate/customer/{username}")
    public ResponseEntity<?> activateCustomer(@PathVariable String username){
        Optional<User> userOptional = userService.findByUsername(username);
        if(userOptional.isPresent()){
            Optional<Customer> customerOptional = customerService.activate(userOptional.orElseThrow());
            if(customerOptional.isPresent()){
                return ResponseEntity.ok(customerOptional.orElseThrow());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/activate/{username}")
    public ResponseEntity<?> activateUser(@PathVariable String username){
        Optional<User> userOptional = userService.activate(username);
        if(userOptional.isPresent()){
            activateChef(username);
            activateCustomer(username);
            return ResponseEntity.ok(userOptional.orElseThrow());

        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/chef/{username}")
    @ResponseBody
    public ResponseEntity<?> deleteChef (@PathVariable String username){
        Optional<User> userOptional =  userService.findByUsername(username);
        if(userOptional.isPresent()){
            Optional<Chef> chefOptional = chefService.delete(userOptional.orElseThrow());
            if(chefOptional.isPresent()){
                return ResponseEntity.ok(chefOptional.orElseThrow() );
            }
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/customer/{username}")
    @ResponseBody
    public ResponseEntity<?> deleteCustomer (@PathVariable String username){
        Optional<User> userOptional =  userService.findByUsername(username);
        if(userOptional.isPresent()){
            Optional<Customer> customerOptional = customerService.delete(userOptional.orElseThrow());
            if(customerOptional.isPresent()){
                return ResponseEntity.ok(customerOptional.orElseThrow() );
            }
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{username}")
    @ResponseBody
    public ResponseEntity<?> delete (@PathVariable String username){
        Optional<User> userOptional =  userService.delete(username);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(userOptional.orElseThrow() );
        }
        return ResponseEntity.notFound().build();
    }
    
    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors); 
    }

    public ResponseEntity<?> updateUserType(int id, User user) {

        User userDB = userService.findById(id).orElseThrow();
        if( customerService.existsByUser(userDB) & user.getType() == 0){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists as a customer");
        }
        if(chefService.existsByUser(userDB) & user.getType() == 1){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists as a chef");
        }
        user = userService.update( id, user).orElseThrow();
        return ResponseEntity.status(HttpStatus.OK).body(user);    
    }

    public boolean validateUser (String username){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();

            if (!username.equals(authenticatedUsername)) {
                return false;
            }
            return true;
        }
    
}
