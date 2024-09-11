package com.rafael.foodly.foodly.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafael.foodly.foodly.entities.Chef;
import com.rafael.foodly.foodly.entities.Customer;
import com.rafael.foodly.foodly.entities.Role;
import com.rafael.foodly.foodly.entities.User;
import com.rafael.foodly.foodly.repositories.RoleRepository;
import com.rafael.foodly.foodly.repositories.UserRepository;



@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private CustomerService customerService;

    @Autowired 
    private ChefService chefService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByState(Integer state) {
        return (List<User>) userRepository.findByState(state);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {

        List<Role> roles = new ArrayList<>();

        String role= roleName(user.getType());
        

        Optional<Role> roleOptional = roleRepository.findByName(role);
        roleOptional.ifPresent(roles::add);
        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userDb = userRepository.save(user);
        roleSettings(user.getType(), userDb );

        return userDb;
    }

    @Override
    @Transactional
    public Optional<User> update(Integer id, User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User userDB = userOptional.orElseThrow();
        
            if(user.getType() != -1){
                String role= roleName(user.getType());
                Optional<Role> roleOptional = roleRepository.findByName(role);
                roleOptional.ifPresent(userDB.getRoles()::add);
                
            }
            userDB.setRoles(userDB.getRoles());
            userDB.setF_name(user.getF_name());
            userDB.setL_name(user.getL_name());
            userDB.setIdentification(user.getIdentification());
            userDB.setBirth_date(user.getBirth_date());
            userDB.setPassword(passwordEncoder.encode(user.getPassword()));
            userDB.setUsername(user.getUsername());
            userDB.setState(user.getState());
            
            userDB = userRepository.save(userDB);
            roleSettings(user.getType(), userDB );

            return Optional.of(userRepository.save(userDB));
        }
        

        return userOptional;

    }

    @Override
    @Transactional
    public Optional<User> delete(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent( userDB -> {
            userDB.setState(0);
            userRepository.save(userDB);

            chefService.findByUser(userDB).ifPresent(chef->{
                chef.setState(0);
                chefService.save(chef);
            });

            customerService.findByUser(userDB).ifPresent(customer->{
                customer.setState(0);
                customerService.save(customer);
            });
        });

        return userOptional;
    }

    @Override
    public Optional<User> activate(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent()){
            userOptional.orElseThrow().setState(1);
            return  Optional.of( userRepository.save(userOptional.orElseThrow()));
        }
        return userOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdentification(String identification) {
        
        return userRepository.existsByIdentification(identification);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String roleName(int type){
        if(type== 0) return "ROLE_CUSTOMER";
        if(type== 1) return "ROLE_CHEF";
        if(type== 2) return "ROLE_ADMIN";
        return "";
    }

    public void roleSettings(int type, User user){
        if(type== 0){
            Customer customer = new Customer();
            customer.setUser(user);
            customerService.save(customer);
            
        } 

        if(type== 1){
            Chef chef = new Chef();
            chef.setUser(user);
            chefService.save(chef);
            
        } 
         
    }


}
