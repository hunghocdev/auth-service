package com.example.authdemo.module.product.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "brand_name", nullable = false)
    @Column(name = "name")
    private String brandName;

    // Quan hệ 1 Brand - N Laptop
    @OneToMany(mappedBy = "brand")
    private List<Laptop> laptops;

    // Constructors, Getters, Setters
    public Brand() {}
    public Brand(String brandName) { this.brandName = brandName; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
}