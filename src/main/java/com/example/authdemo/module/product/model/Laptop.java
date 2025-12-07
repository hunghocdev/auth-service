package com.example.authdemo.module.product.model;

import com.example.authdemo.common.model.BaseEntity; // Cần import lớp cơ sở

import jakarta.persistence.*;

@Entity
@Table(name = "laptops")
// Kế thừa BaseEntity
public class Laptop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Giữ lại ID vì ID không nằm trong BaseEntity

    private String name;
    private Double price;
    private String description;

    @Column(name = "is_deleted")
    private boolean isDeleted = false; // Mặc định là chưa xóa

    // Quan hệ N Laptop - 1 Brand
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // Constructors
    public Laptop() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... (Giữ nguyên các Getters/Setters khác)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    // Lưu ý: Các getters cho createdAt và updatedAt được thừa kế từ BaseEntity.
}