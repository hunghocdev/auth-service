package com.example.secureauthservice.module.product.model;

import com.example.secureauthservice.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "laptops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Inheriting from BaseEntity to reuse auditing fields (createdAt, updatedAt, createdBy, updatedBy)
public class Laptop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String name;

    /**
     * Using BigDecimal for price to ensure precision in PostgreSQL DECIMAL(15,2).
     * This avoids rounding errors common with Double/Float.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    /**
     * Note: createdAt, updatedAt, createdBy, and updatedBy fields
     * are now inherited from BaseEntity and should not be declared here.
     */
}