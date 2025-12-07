package com.example.authdemo.module.product.repository;

import com.example.authdemo.module.product.model.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// JpaSpecificationExecutor giúp thực hiện câu query động (Dynamic Query)
public interface LaptopRepository extends JpaRepository<Laptop, Long>, JpaSpecificationExecutor<Laptop> {
}