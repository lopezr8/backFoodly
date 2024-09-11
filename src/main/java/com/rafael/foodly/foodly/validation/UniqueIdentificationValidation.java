package com.rafael.foodly.foodly.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rafael.foodly.foodly.services.UserService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueIdentificationValidation implements ConstraintValidator<UniqueIdentification,String> {
    
    @Autowired
    private UserService userService;
    

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (userService == null) {
            return true;
        }

        return !userService.existsByIdentification(value);
    }

}
