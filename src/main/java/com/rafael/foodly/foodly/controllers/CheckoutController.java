package com.rafael.foodly.foodly.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafael.foodly.foodly.entities.Checkout;
import com.rafael.foodly.foodly.entities.Chef;
import com.rafael.foodly.foodly.entities.Customer;
import com.rafael.foodly.foodly.entities.Dish;
import com.rafael.foodly.foodly.entities.User;
import com.rafael.foodly.foodly.services.CheckoutService;
import com.rafael.foodly.foodly.services.ChefService;
import com.rafael.foodly.foodly.services.CustomerService;
import com.rafael.foodly.foodly.services.DishService;
import com.rafael.foodly.foodly.services.UserService;

import jakarta.validation.Valid;

//! status checkout-    0.generado  1.enviado 2.confirmado


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/foodly/checkout")
public class CheckoutController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private ChefService chefService;

    @GetMapping
    public List<Checkout> list(){
        return checkoutService.findAll();
    }

    @GetMapping("/{username}/{checkoutId}")
    public ResponseEntity<?> viewCheckout (@PathVariable int checkoutId, @PathVariable String username){

        if(!validateUser(username)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
        }

        Optional<Checkout> checkOptional =  checkoutService.findById(checkoutId);
        if(checkOptional.isPresent()){
            if(checkOptional.orElseThrow().getCustomer().getUsername().equals(username)){
                return ResponseEntity.ok(checkOptional.orElseThrow() );
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Chekout does not belong to user");

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checkout not found");
    }

    @GetMapping("/chef/{username}")
    public ResponseEntity<?> chefCheckouts ( @PathVariable String username){

        if(!validateUser(username)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
        }

        Optional<User> optionalUser = userService.findByUsername(username);
        if(optionalUser.isPresent()){
            Optional<Chef> optionalChef =  chefService.findByUser(optionalUser.orElseThrow());
            if(optionalChef.isPresent()){
                List<Checkout> chekouts =  checkoutService.findByChef(optionalUser.orElseThrow());
                return ResponseEntity.ok(chekouts);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checkout not found");
    }

    @GetMapping("/customer/{username}")
    public ResponseEntity<?> customerCheckouts ( @PathVariable String username){

        if(!validateUser(username)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
        }

        Optional<User> optionalUser = userService.findByUsername(username);
        if(optionalUser.isPresent()){
            Optional<Customer> optionalCustomer =  customerService.findByUser(optionalUser.orElseThrow());
            if(optionalCustomer.isPresent()){
                List<Checkout> chekouts =  checkoutService.findByCustomer(optionalUser.orElseThrow());
                return ResponseEntity.ok(chekouts);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checkout not found");
    }

    
    
    @PostMapping("/chef/{checkoutId}/{score}")
    public ResponseEntity<?> chefScore(@PathVariable int checkoutId, @PathVariable double score  ){
        Optional<Checkout> optionalCheckout = checkoutService.findById(checkoutId); 
        if(!optionalCheckout.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checkout not found");
        }
        if(score < 0 || score > 5){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid score");
        }
        chefService.findByUser( optionalCheckout.orElseThrow().getChef()  ).ifPresent( chef -> {
            if(chef.getScore() == 0){
                chef.setScore(score);
                chefService.save(chef);
                return;
            }
            chef.setScore( (chef.getScore() + score) / 2);
            chefService.save(chef);
            optionalCheckout.orElseThrow().setRate_chef(1);
            checkoutService.save(optionalCheckout.orElseThrow());
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(optionalCheckout.orElseThrow());
    }

    @PostMapping("/rate-finish/{checkoutId}")
    public ResponseEntity<?> rateFinish(@PathVariable int checkoutId  ){
        Optional<Checkout> optionalCheckout = checkoutService.findById(checkoutId); 
        if(!optionalCheckout.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checkout not found");
        }
        
        optionalCheckout.orElseThrow().setState(3);
        checkoutService.save(optionalCheckout.orElseThrow());
        return ResponseEntity.status(HttpStatus.CREATED).body(optionalCheckout.orElseThrow());
    }

    @PostMapping("/customer/{checkoutId}/{score}")
    public ResponseEntity<?> customerScore(@PathVariable int checkoutId, @PathVariable double score){
        Optional<Checkout> optionalCheckout = checkoutService.findById(checkoutId); 
        if(!optionalCheckout.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checkout not found");
        }
        if(score < 0 || score > 5){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid score");
        }
        customerService.findByUser(optionalCheckout.orElseThrow().getCustomer() ).ifPresent( customer -> {
            if(customer.getScore() == 0){
                customer.setScore(score);
                customerService.save(customer);
                return;
            }
            customer.setScore( (customer.getScore() + score) / 2);
            customerService.save(customer);
            optionalCheckout.orElseThrow().setRate_customer(1);
            checkoutService.save(optionalCheckout.orElseThrow());
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(optionalCheckout.orElseThrow());

    }

    @PostMapping("/{username}/{dishId}")
    public ResponseEntity<?> create(@Valid @RequestBody Checkout checkout, BindingResult result, @PathVariable String username, 
    @PathVariable int dishId){
        if(result.hasFieldErrors()){
            return validation(result);
        }
        
        if(!validateUser(username)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
        }

        Optional<User> optionalUser = userService.findByUsername(username);
        if(optionalUser.isPresent()){
            Optional<Customer> optionalCustomer =  customerService.findByUser(optionalUser.orElseThrow());
            if(optionalCustomer.isPresent()){
                if(optionalCustomer.orElseThrow().getState() == 0){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Customer is inactive ");
                }

                checkout.setCustomer(optionalUser.orElseThrow());

                Optional<Dish> optionalDish = dishService.findById(dishId);
                if(!optionalDish.isPresent()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dish not found");
                }   
                if(optionalDish.orElseThrow().getUnits() < checkout.getUnits()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough units available, only"+ optionalDish.orElseThrow().getUnits() + " available" );
                }

                optionalDish.orElseThrow().setUnits(optionalDish.orElseThrow().getUnits() - checkout.getUnits());
                dishService.save(optionalDish.orElseThrow());

                checkout.setDate(LocalDateTime.now());
                checkout.setDish(optionalDish.orElseThrow());
                checkout.setChef(optionalDish.orElseThrow().getChef());
                checkout.setValue(optionalDish.orElseThrow().getValue() * checkout.getUnits() );
                checkout.setState(0);
                Checkout checkoutDB =  checkoutService.save(checkout);
                
                return ResponseEntity.status(HttpStatus.CREATED).body(checkoutDB);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        
        
    }

    @PostMapping("/finish/{username}/{checkoutId}")
    public ResponseEntity<?> createF(  @PathVariable String username, 
    @PathVariable int checkoutId){
        
        
        if(!validateUser(username)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Username mismatch ");
        }

        Optional<User> optionalUser = userService.findByUsername(username);
        if(optionalUser.isPresent()){
            Optional<Customer> optionalCustomer =  customerService.findByUser(optionalUser.orElseThrow());
            if(optionalCustomer.isPresent()){
                if(optionalCustomer.orElseThrow().getState() == 0){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Customer is inactive ");
                }

                Optional<Checkout> optionalCheckout = checkoutService.findById(checkoutId);
                if(!optionalCheckout.isPresent()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checkout not found");
                }

                chefService.findByUser( optionalCheckout.orElseThrow().getChef() ).ifPresent( chef -> {
                    chef.setSales( chef.getSales() + 1);
                    chef.setIncome( chef.getIncome() + optionalCheckout.orElseThrow().getValue());
                    chefService.save(chef);
                });

                optionalCheckout.orElseThrow().setState(2);
                Checkout checkoutDB =  checkoutService.save(optionalCheckout.orElseThrow());

                return ResponseEntity.status(HttpStatus.CREATED).body( checkoutDB);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        
        
    }

    @PostMapping("/send/{username}/{checkoutId}")
    public ResponseEntity<?> createS(  @PathVariable String username, 
    @PathVariable int checkoutId){
        
        
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

                Optional<Checkout> optionalCheckout = checkoutService.findById(checkoutId);
                if(!optionalCheckout.isPresent()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checkout not found");
                }

                optionalCheckout.orElseThrow().setState(1);

                Checkout checkoutDB =  checkoutService.save(optionalCheckout.orElseThrow());

                return ResponseEntity.status(HttpStatus.CREATED).body( checkoutDB);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
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
