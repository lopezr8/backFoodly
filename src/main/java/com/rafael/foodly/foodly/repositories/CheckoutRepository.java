package com.rafael.foodly.foodly.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.rafael.foodly.foodly.entities.Checkout;
import com.rafael.foodly.foodly.entities.User;


public interface CheckoutRepository extends CrudRepository<Checkout, Integer>{

    Optional<Checkout> findById(int id);

    List<Checkout> findByChef(User chef);

    List<Checkout> findByCustomer(User customer);
 

}
