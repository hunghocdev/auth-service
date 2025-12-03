// JPA Entity
//**** Lưu ý của class này ****
// password không phải là plaintext. Để phản ánh đúng phải đặt tên passwordHash
// @UniqueConstraint → thuộc database layer, không phải validation logic.


package com.example.authdemo.user;

import jakarta.persistence.*;   // Import các annotation của JPA để làm việc với database.
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.password.PasswordEncoder;

// ======Khai báo Entity và cấu hình Table========
@Entity     //Báo với JPA: Class này là một bảng trong database.
@Table(name = "users", uniqueConstraints = {          // Xác định tên bảng là users
        @UniqueConstraint(columnNames = "username"),    //username và email không được trùng nhau
        @UniqueConstraint(columnNames = "email")
})

//========Primary key===========
//==========  DTO ==========
public class User {
    @Id     // đây là primary key của bản
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // tự động tăng lên mỗi khi một hàng (row) mới được lưu thành công vào bảng
    private Long id;

    @Column(nullable = false, length = 50)      // username không được để trống và độ dài tối đa 50
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @JsonIgnore // Ensure the password hash is never serialized to JSON
    @Column(name = "password_hash", nullable = false, length = 255)     //tên cột trong DB là password_hash
    private String passwordHash;

    public User() {}    // hàm tạo rỗng bắt buộc cho JPA.

    //constructer lưu dữ liệu vào database
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    //===================getters và setters=============
    // provide a domain method to verify instead
    // No public getter or setter for passwordHash to avoid accidental exposure.

    // Domain method to verify the plaintext password against the stored hash.
    // Keeps verification logic inside the entity; prevents accidental exposure of the hash.
    public boolean verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (rawPassword == null || passwordHash == null) return false;
        return passwordEncoder.matches(rawPassword, this.passwordHash);
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
