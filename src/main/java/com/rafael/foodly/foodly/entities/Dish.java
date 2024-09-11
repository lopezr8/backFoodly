package com.rafael.foodly.foodly.entities;



import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "dishes")
public class Dish implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "chef")
    @JsonIgnoreProperties({"id_user","identification","f_name","l_name","state","birth_date","address","roles","hibernateLazyInitializer","handler"})
    private User chef;

    private double value;

    private String name;

    private String image;
    
    private int units;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnoreProperties({"dish","hibernateLazyInitializer","handler"})
    private List<DishDescription> descriptions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<DishDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DishDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public User getChef() {
        return chef;
    }

    public void setChef(User chef) {
        this.chef = chef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    } 


}
