package com.rafael.foodly.foodly.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.rafael.foodly.foodly.services.UserService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueUsernameValidation implements HandlerInterceptor, ConstraintValidator<UniqueUsername, String>{

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (userService == null) {
            return true;
        }
        return !userService.existsByUsername(value);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }



    

}
