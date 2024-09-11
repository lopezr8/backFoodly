package com.rafael.foodly.foodly.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafael.foodly.foodly.entities.Checkout;
import com.rafael.foodly.foodly.entities.User;
import com.rafael.foodly.foodly.repositories.CheckoutRepository;

@Service
public class CheckoutServiceImpl implements CheckoutService{

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private CheckoutDescriptionService checkoutDescriptionService;
    

    @Override
    @Transactional(readOnly = true)
    public List<Checkout> findAll() {
        return (List<Checkout>) checkoutRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Checkout> findById(Integer id) {
        return checkoutRepository.findById(id);
    }

    @Override
    public Checkout save(Checkout checkout) {
        Checkout checkoutDb = checkoutRepository.save(checkout);
        checkout.getDescriptions().forEach( checkoutDescription ->{
            checkoutDescription.setCheckout(checkoutDb);
            checkoutDescriptionService.save(checkoutDescription);
        });

        return checkoutDb;
    }

    @Override
    public Optional<Checkout> delete(Integer id) {
        Optional<Checkout> checkOptional = checkoutRepository.findById(id);
        checkOptional.ifPresent( userDB -> {
            checkoutRepository.deleteById(id);
        });

        return checkOptional;
    }

    @Override
    public List<Checkout> findByChef(User chef) {
        return checkoutRepository.findByChef(chef);
    }

    @Override
    public List<Checkout> findByCustomer(User chef) {
        return checkoutRepository.findByCustomer(chef);
    }



}
