package com.example.secureauthservice.module.product.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO for returning laptop information to the client.
 * Uses Lombok to minimize boilerplate and provide a fluent Builder API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaptopResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String brandName;
}