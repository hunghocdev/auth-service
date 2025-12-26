package com.example.authdemo.module.user.repository;

import com.example.authdemo.module.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Tìm kiếm Role theo tên chính xác trong Database.
     * @param name Tên role (ví dụ: ROLE_USER, ROLE_ADMIN)
     * @return Optional chứa Role nếu tìm thấy
     */
    Optional<Role> findByName(String name);
}