package com.example.authdemo.module.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for updating user profile information.
 * Uses Lombok to reduce boilerplate code.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @Size(max = 100, message = "Full name is too long")
    private String fullName;

    @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be numeric, between 10-15 characters")
    private String phoneNumber;

    private String address;

    /**
     * Date of birth in dd-MM-yyyy format (e.g., 30-01-2000).
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;
    private String gender;
}