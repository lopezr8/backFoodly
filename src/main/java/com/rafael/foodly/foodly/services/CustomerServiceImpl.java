package com.rafael.foodly.foodly.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafael.foodly.foodly.entities.Customer;
import com.rafael.foodly.foodly.entities.User;
import com.rafael.foodly.foodly.repositories.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return (List<Customer>) customerRepository.findByState(1);
    }
    
    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public boolean existsByUser(User user) {
        return customerRepository.existsByUser(user);
    }

    @Override
    public Optional<Customer> findByUser(User user) {
        return customerRepository.findByUser(user);
    }

    @Override
    public Optional<Customer> activate(User user) {
        Optional<Customer> customerOptional = customerRepository.findByUser(user);
        if(customerOptional.isPresent()){
            customerOptional.orElseThrow().setState(1);
            return  Optional.of( customerRepository.save(customerOptional.orElseThrow()));
        }
        return customerOptional;
    }


    @Override
    public Optional<Customer> delete(User user) {
        Optional<Customer> optionalCustomer = customerRepository.findByUser(user);
        if(optionalCustomer.isPresent()){
            Customer CustomerDB = optionalCustomer.orElseThrow();
            CustomerDB.setState(0);
            return Optional.of(customerRepository.save(CustomerDB));
        }
        return optionalCustomer;
    }


}
