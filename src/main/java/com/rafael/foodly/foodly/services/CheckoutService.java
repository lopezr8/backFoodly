package com.rafael.foodly.foodly.services;

import java.util.List;
import java.util.Optional;

import com.rafael.foodly.foodly.entities.Checkout;
import com.rafael.foodly.foodly.entities.User;

public interface CheckoutService {

    Checkout save (Checkout checkout);

    Optional<Checkout> delete(Integer id);

    List<Checkout> findAll();

    Optional<Checkout> findById(Integer id);

    List<Checkout> findByChef(User chef);

    List<Checkout> findByCustomer(User customer);
}
