package com.example.secureauthservice.module.product.repository;

import com.example.secureauthservice.module.product.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}