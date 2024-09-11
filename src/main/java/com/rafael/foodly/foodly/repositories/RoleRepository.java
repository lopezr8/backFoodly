package com.rafael.foodly.foodly.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.rafael.foodly.foodly.entities.Role;



public interface RoleRepository extends CrudRepository<Role, Integer>{

    Optional<Role> findByName(String name);
}
