package com.example.authdemo.module.product.service;

import com.example.authdemo.module.product.model.Brand;
import com.example.authdemo.module.product.model.Laptop;
import com.example.authdemo.module.product.repository.BrandRepository;
import com.example.authdemo.module.product.repository.LaptopRepository;
import com.example.authdemo.module.product.dto.LaptopRequest;
import com.example.authdemo.module.product.dto.LaptopResponse;
import com.example.authdemo.module.product.dto.PageResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LaptopService {
    private final LaptopRepository laptopRepository;
    private final BrandRepository brandRepository;

    public LaptopService(LaptopRepository laptopRepository, BrandRepository brandRepository) {
        this.laptopRepository = laptopRepository;
        this.brandRepository = brandRepository;
    }

    // 1. Create
    public LaptopResponse create(LaptopRequest req) {
        Brand brand = brandRepository.findById(req.getBrandId())
                .orElseThrow(() -> new NoSuchElementException("Brand not found"));

        Laptop laptop = new Laptop();
        laptop.setName(req.getName());
        laptop.setPrice(req.getPrice());
        laptop.setDescription(req.getDescription());
        laptop.setBrand(brand);
        laptop.setDeleted(false);

        Laptop saved = laptopRepository.save(laptop);
        return toResponse(saved);
    }

    // 2. Get One
    public LaptopResponse getOne(Long id) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Laptop not found"));

        if (laptop.isDeleted()) {
            throw new NoSuchElementException("Laptop has been deleted");
        }
        return toResponse(laptop);
    }

    // 3. Get Many (Search, Filter, Pagination, Sort)
    public PageResponse<LaptopResponse> getMany(
            String keyword, Double minPrice, Double maxPrice, Long brandId,
            int page, int size, String sortBy, String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Build Dynamic Query (Specification)
        Specification<Laptop> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Luôn luôn lọc isDeleted = false
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            // Search by Name
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + keyword + "%"));
            }
            // Filter by Brand
            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), brandId));
            }
            // Filter by Price Range
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Laptop> pageResult = laptopRepository.findAll(spec, pageable);

        List<LaptopResponse> responseList = pageResult.getContent().stream()
                .map(this::toResponse).toList();

        return new PageResponse<>(
                responseList,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    // 4. Update
    public LaptopResponse update(Long id, LaptopRequest req) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Laptop not found"));

        if (req.getBrandId() != null) {
            Brand brand = brandRepository.findById(req.getBrandId())
                    .orElseThrow(() -> new NoSuchElementException("Brand not found"));
            laptop.setBrand(brand);
        }
        if (req.getName() != null) laptop.setName(req.getName());
        if (req.getPrice() != null) laptop.setPrice(req.getPrice());
        if (req.getDescription() != null) laptop.setDescription(req.getDescription());

        return toResponse(laptopRepository.save(laptop));
    }

    // 5. Soft Delete
    public void softDelete(Long id) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Laptop not found"));
        laptop.setDeleted(true);
        laptopRepository.save(laptop);
    }

    private LaptopResponse toResponse(Laptop laptop) {
        return new LaptopResponse(
                laptop.getId(),
                laptop.getName(),
                laptop.getPrice(),
                laptop.getDescription(),
                laptop.getBrand() != null ? laptop.getBrand().getBrandName() : "Unknown"
        );
    }
}