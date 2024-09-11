package com.rafael.foodly.foodly.services;


import java.util.List;
import java.util.Optional;

import com.rafael.foodly.foodly.entities.Customer;
import com.rafael.foodly.foodly.entities.User;

public interface CustomerService {
    Customer save(Customer customer);

    boolean existsByUser(User user);

    Optional<Customer> findByUser(User user);

    Optional<Customer> activate(User user);

    List<Customer> findAll();

    Optional<Customer> delete(User user);
}
