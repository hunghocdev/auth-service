package com.example.authdemo.module.product.repository;

import com.example.authdemo.module.product.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}