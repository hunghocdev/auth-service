package com.example.secureauthservice.module.product.controller;

import com.example.secureauthservice.module.product.service.LaptopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.secureauthservice.module.product.dto.LaptopRequest;
import com.example.secureauthservice.module.product.dto.LaptopResponse;
import com.example.secureauthservice.module.product.dto.PageResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/laptops")
public class LaptopController {
    private final LaptopService laptopService;

    // Constructor
    public LaptopController(LaptopService laptopService) {
        this.laptopService = laptopService;
    }

    // POST /api/laptops
    /**
     * Creates a new laptop record (CREATE operation).
     * @return The created laptop object.
     */
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LaptopResponse> create(@Valid @RequestBody LaptopRequest req) {
        return ResponseEntity.ok(laptopService.create(req));
    }

    // GET /api/laptops/{id}
    /**
     * Retrieves a single laptop by its ID (READ operation - GetOne).
     * Excludes records where is_deleted = true.
     * @param id The unique ID of the laptop.
     * @return The specific laptop object.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LaptopResponse> getOne(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(laptopService.getOne(id));
    }

    // GET /api/laptops
    /**
     * Retrieves a list of laptops with extensive filtering and pagination (READ operation - GetMany).
     * Supports search (keyword), price filtering, brand filtering, sorting, and pagination.
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<LaptopResponse>> getMany(
            @RequestParam(required = false) String keyword, // Search term (e.g., in name or description)
            @RequestParam(required = false) Double minPrice, // Filter by minimum price
            @RequestParam(required = false) Double maxPrice, // Filter by maximum price
            @RequestParam(required = false) Long brandId,    // Filter by brand ID
            @RequestParam(defaultValue = "0") int page,       // Page number for pagination
            @RequestParam(defaultValue = "10") int size,      // Number of items per page
            @RequestParam(defaultValue = "id") String sortBy,  // Field to sort by
            @RequestParam(defaultValue = "asc") String sortDir // Sort direction (asc/desc)
    ) {
        return ResponseEntity.ok(laptopService.getMany(keyword, minPrice, maxPrice, brandId, page, size, sortBy, sortDir));
    }

    // PUT /api/laptops/{id}
    /**
     * Updates an existing laptop record by ID (UPDATE operation).
     * @return The updated laptop object.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LaptopResponse> update(@PathVariable Long id, @RequestBody LaptopRequest req) {
        return ResponseEntity.ok(laptopService.update(id, req));
    }

    // DELETE /api/laptops/{id}
    /**
     * Performs a Soft Delete on a laptop record.
     * It updates the database record by setting 'is_deleted = true'.
     * @param id The unique ID of the laptop to soft delete.
     * @return 204 No Content status upon successful operation.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        laptopService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}