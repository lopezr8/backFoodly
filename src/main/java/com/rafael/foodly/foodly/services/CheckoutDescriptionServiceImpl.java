package com.rafael.foodly.foodly.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafael.foodly.foodly.entities.CheckoutDescription;
import com.rafael.foodly.foodly.repositories.CheckoutDescriptionRepository;

@Service
public class CheckoutDescriptionServiceImpl implements CheckoutDescriptionService{

    @Autowired
    private CheckoutDescriptionRepository checkoutDescriptionRepository;

    @Override
    public CheckoutDescription save(CheckoutDescription checkoutDescription) {
        return checkoutDescriptionRepository.save(checkoutDescription);
    }

}
