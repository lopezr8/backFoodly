package com.rafael.foodly.foodly.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.rafael.foodly.foodly.entities.Customer;
import com.rafael.foodly.foodly.entities.User;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
    boolean  existsByUser(User user);

    Optional<Customer> findByUser(User user);

    List<Customer> findByState(int state);
}
