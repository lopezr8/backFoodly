package com.rafael.foodly.foodly.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "checkout")
public class Checkout implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="customer")
    @JsonIgnoreProperties({"id_user","identification","f_name","l_name","state","birth_date","address","roles","hibernateLazyInitializer","handler"})
    private User customer;

    @OneToMany
    @JsonIgnoreProperties({"checkout","hibernateLazyInitializer","handler"})
    @JoinColumn(name = "checkout")
    private List<CheckoutDescription> descriptions;
    
    @ManyToOne
    @JsonIgnoreProperties({"units","value","chef","descriptions","hibernateLazyInitializer","handler"})
    @JoinColumn(name = "dish", referencedColumnName = "id")
    private Dish dish; 

    @ManyToOne
    @JoinColumn(name="chef")
    @JsonIgnoreProperties({"id_user","identification","f_name","l_name","state","birth_date","address","roles","hibernateLazyInitializer","handler"})
    private User chef;

    private int rate_chef;

    private int rate_customer;

    private double value;

    private int units; 

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    

    public List<CheckoutDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<CheckoutDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getChef() {
        return chef;
    }

    public void setChef(User chef) {
        this.chef = chef;
    }

    public int getRate_chef() {
        return rate_chef;
    }

    public void setRate_chef(int rate_chef) {
        this.rate_chef = rate_chef;
    }

    public int getRate_customer() {
        return rate_customer;
    }

    public void setRate_customer(int rate_customer) {
        this.rate_customer = rate_customer;
    }

    
}
