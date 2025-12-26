// Mô tả: UserRepository là tầng làm việc với DB
// Nhiệm vụ chính: Cung cấp các hàm để truy vấn database liên quan đến bảng users.
/* UserRepository là interface giúp bạn truy vấn database thông qua Entity User.
    Các method như findByUsername hoặc existsByEmail được Spring tự tạo SQL.
    Repository không chứa logic xử lý, chỉ phục vụ truy vấn DB.*/

package com.example.authdemo.module.user.repository;

import com.example.authdemo.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
