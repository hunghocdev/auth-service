package com.example.authdemo.module.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

// Request khi tạo/sửa Laptop
public class LaptopRequest {

    // Tên không được null, không được trống và phải có độ dài từ 3 đến 255 ký tự.
    @NotBlank(message = "Tên sản phẩm không được để trống.")
    @Size(min = 3, max = 255, message = "Tên sản phẩm phải từ 3 đến 255 ký tự.")
    private String name;

    // Giá không được null và phải là số không âm.
    @NotNull(message = "Giá sản phẩm không được để trống.")
    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0.")
    private Double price;

    // Mô tả có thể null, nhưng nếu có thì không quá 1000 ký tự.
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự.")
    private String description;

    // brandId không được null.
    @NotNull(message = "ID thương hiệu không được để trống.")
    private Long brandId;

    // Getters Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
}