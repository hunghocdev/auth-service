package com.example.authdemo.module.product.dto;

// Response trả về client
public class LaptopResponse {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private String brandName; // Chỉ trả về tên Brand thay vì cả object Brand

    public LaptopResponse(Long id, String name, Double price, String description, String brandName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.brandName = brandName;
    }

    // Getters...
    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getBrandName() { return brandName; }
}